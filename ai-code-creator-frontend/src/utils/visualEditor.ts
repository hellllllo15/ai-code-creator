/**
 * 可视化编辑工具
 * 用于处理 iframe 通信和元素选择逻辑
 */

export interface SelectedElementInfo {
  tagName: string;
  className: string;
  id: string;
  textContent: string;
  selector: string; // 完整的选择器路径
  pagePath: string; // 页面路径（hash 和 search）
  rect: {
    top: number;
    left: number;
    width: number;
    height: number;
  };
  styles?: Record<string, string>;
  attributes?: Record<string, string>; // 元素的重要属性
}

/**
 * 注入可视化编辑代码到 iframe
 */
export function injectVisualEditorScript(iframe: HTMLIFrameElement): () => void {
  const iframeDoc = iframe.contentDocument;
  if (!iframeDoc) {
    console.warn('无法访问 iframe 文档，可能不同源');
    return () => {};
  }

  // 创建样式
  const style = iframeDoc.createElement('style');
  style.textContent = `
    .visual-editor-hover {
      outline: 2px solid #3b82f6 !important;
      outline-offset: 2px !important;
      cursor: pointer !important;
      background-color: rgba(59, 130, 246, 0.1) !important;
    }
    .visual-editor-selected {
      outline: 3px solid #1d4ed8 !important;
      outline-offset: 2px !important;
      background-color: rgba(29, 78, 216, 0.15) !important;
    }
  `;
  iframeDoc.head.appendChild(style);

  // 创建脚本
  const script = iframeDoc.createElement('script');
  script.textContent = `
    (function() {
      let isEditModeActive = true;
      let currentHovered = null;
      let currentSelected = null;
      let mouseoverHandler = null;
      let mouseoutHandler = null;
      let clickHandler = null;
      
      // 生成完整的选择器路径（从body到目标元素）
      function generateSelector(element) {
        const path = [];
        let current = element;
        
        while (current && current !== document.body && current !== document.documentElement) {
          let selector = current.tagName.toLowerCase();
          
          // 优先使用 ID
          if (current.id) {
            selector += '#' + current.id;
            path.unshift(selector);
            break; // ID 是唯一的，可以停止
          }
          
          // 使用类名
          if (current.className && typeof current.className === 'string') {
            const classes = current.className.split(' ')
              .filter(c => c.trim() && !c.startsWith('visual-editor-'));
            if (classes.length > 0) {
              selector += '.' + classes.join('.');
            }
          }
          
          // 使用 nth-child 定位
          if (current.parentElement) {
            const siblings = Array.from(current.parentElement.children);
            const index = siblings.indexOf(current) + 1;
            selector += ':nth-child(' + index + ')';
          }
          
          path.unshift(selector);
          current = current.parentElement;
        }
        
        return path.join(' > ');
      }
      
      // 获取元素信息
      function getElementInfo(element) {
        const rect = element.getBoundingClientRect();
        const styles = window.getComputedStyle(element);
        
        // 获取页面路径（hash 和 search），过滤掉 _t 参数
        let search = window.location.search;
        if (search) {
          const params = new URLSearchParams(search);
          params.delete('_t'); // 移除时间戳参数
          search = params.toString() ? '?' + params.toString() : '';
        }
        let pagePath = search + window.location.hash;
        if (!pagePath) {
          pagePath = '#/'; // 默认路径
        }
        
        // 获取元素的重要属性
        const attributes = {};
        const importantAttrs = ['href', 'src', 'alt', 'title', 'type', 'value', 'placeholder', 'name'];
        importantAttrs.forEach(attr => {
          if (element.hasAttribute && element.hasAttribute(attr)) {
            attributes[attr] = element.getAttribute(attr) || '';
          }
        });
        
        return {
          tagName: element.tagName.toLowerCase(),
          className: element.className || '',
          id: element.id || '',
          textContent: (element.textContent || '').trim().substring(0, 200),
          selector: generateSelector(element),
          pagePath: pagePath,
          rect: {
            top: Math.round(rect.top),
            left: Math.round(rect.left),
            width: Math.round(rect.width),
            height: Math.round(rect.height)
          },
          styles: {
            color: styles.color,
            backgroundColor: styles.backgroundColor,
            fontSize: styles.fontSize,
            fontWeight: styles.fontWeight,
            padding: styles.padding,
            margin: styles.margin,
          },
          attributes: attributes
        };
      }
      
      // 清除所有高亮效果
      function clearAllHighlights() {
        // 清除悬浮高亮
        if (currentHovered) {
          currentHovered.classList.remove('visual-editor-hover');
          currentHovered = null;
        }
        // 清除选中高亮
        if (currentSelected) {
          currentSelected.classList.remove('visual-editor-selected');
          currentSelected = null;
        }
        // 清除所有可能遗留的高亮样式
        const hoverElements = document.querySelectorAll('.visual-editor-hover');
        hoverElements.forEach(el => el.classList.remove('visual-editor-hover'));
        const selectedElements = document.querySelectorAll('.visual-editor-selected');
        selectedElements.forEach(el => el.classList.remove('visual-editor-selected'));
      }
      
      // 鼠标悬浮事件
      mouseoverHandler = (event) => {
        if (!isEditModeActive) return;
        
        const target = event.target;
        if (!target || target === document.body || target === document.documentElement) {
          return;
        }
        
        // 移除之前的高亮
        if (currentHovered && currentHovered !== currentSelected) {
          currentHovered.classList.remove('visual-editor-hover');
        }
        
        // 如果不是已选中的元素，添加高亮
        if (target !== currentSelected) {
          currentHovered = target;
          target.classList.add('visual-editor-hover');
        }
      };
      
      // 鼠标离开事件
      mouseoutHandler = (event) => {
        if (!isEditModeActive) return;
        
        const target = event.target;
        if (!target || target === document.body || target === document.documentElement) {
          return;
        }
        
        // 移除高亮（如果不是已选中的）
        if (target === currentHovered && target !== currentSelected) {
          target.classList.remove('visual-editor-hover');
          currentHovered = null;
        }
      };
      
      // 点击事件
      clickHandler = (event) => {
        if (!isEditModeActive) return;
        
        event.preventDefault();
        event.stopPropagation();
        
        const target = event.target;
        if (!target || target === document.body || target === document.documentElement) {
          return;
        }
        
        // 移除之前选中元素的样式
        if (currentSelected && currentSelected !== target) {
          currentSelected.classList.remove('visual-editor-selected');
          currentSelected.classList.remove('visual-editor-hover');
        }
        
        // 添加选中样式
        if (currentSelected !== target) {
          currentSelected = target;
          target.classList.remove('visual-editor-hover');
          target.classList.add('visual-editor-selected');
          
          // 获取元素信息并发送给父窗口
          const elementInfo = getElementInfo(target);
          
          window.parent.postMessage({
            type: 'element-selected',
            payload: elementInfo
          }, '*');
        }
      };
      
      // 添加事件监听器
      document.addEventListener('mouseover', mouseoverHandler, true);
      document.addEventListener('mouseout', mouseoutHandler, true);
      document.addEventListener('click', clickHandler, true);
      
      // 监听父窗口消息，用于控制编辑模式
      window.addEventListener('message', (event) => {
        if (event.data && event.data.type === 'visual-editor-toggle') {
          isEditModeActive = event.data.active || false;
          if (!isEditModeActive) {
            clearAllHighlights();
          }
        }
      });
      
      // 清理函数（会在移除时调用）
      window.__visualEditorCleanup = function() {
        // 禁用编辑模式
        isEditModeActive = false;
        
        // 移除事件监听器
        if (mouseoverHandler) {
          document.removeEventListener('mouseover', mouseoverHandler, true);
        }
        if (mouseoutHandler) {
          document.removeEventListener('mouseout', mouseoutHandler, true);
        }
        if (clickHandler) {
          document.removeEventListener('click', clickHandler, true);
        }
        
        // 清除所有高亮
        clearAllHighlights();
        
        // 移除样式和脚本
        if (style && style.parentNode) {
          style.parentNode.removeChild(style);
        }
        if (script && script.parentNode) {
          script.parentNode.removeChild(script);
        }
      };
    })();
  `;
  iframeDoc.head.appendChild(script);

  // 返回清理函数
  return () => {
    try {
      const iframeDoc = iframe.contentDocument;
      if (iframeDoc && iframeDoc.defaultView && (iframeDoc.defaultView as any).__visualEditorCleanup) {
        (iframeDoc.defaultView as any).__visualEditorCleanup();
      }
    } catch (e) {
      console.warn('清理可视化编辑脚本失败:', e);
    }
  };
}

/**
 * 移除可视化编辑功能
 */
export function removeVisualEditorScript(iframe: HTMLIFrameElement): void {
  try {
    const iframeDoc = iframe.contentDocument;
    if (!iframeDoc) return;

    // 移除所有可视化编辑相关的样式和脚本
    const styleElements = iframeDoc.querySelectorAll('style');
    styleElements.forEach((style) => {
      if (style.textContent && style.textContent.includes('visual-editor')) {
        style.remove();
      }
    });

    const scriptElements = iframeDoc.querySelectorAll('script');
    scriptElements.forEach((script) => {
      if (script.textContent && script.textContent.includes('visual-editor')) {
        script.remove();
      }
    });

    // 移除所有元素上的类
    const elements = iframeDoc.querySelectorAll('.visual-editor-hover, .visual-editor-selected');
    elements.forEach((el) => {
      el.classList.remove('visual-editor-hover', 'visual-editor-selected');
    });

    // 调用清理函数
    if (iframeDoc.defaultView && (iframeDoc.defaultView as any).__visualEditorCleanup) {
      (iframeDoc.defaultView as any).__visualEditorCleanup();
    }
  } catch (e) {
    console.warn('移除可视化编辑功能失败:', e);
  }
}

