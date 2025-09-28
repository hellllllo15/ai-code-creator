document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.querySelector('.login-form');
    
    loginForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        
        if (username && password) {
            // 模拟登录验证
            if (username === 'admin' && password === '123456') {
                alert('登录成功！');
                // 实际应用中这里会跳转到主页
            } else {
                alert('用户名或密码错误！');
            }
        } else {
            alert('请填写完整信息！');
        }
    });
    
    // 简单的输入框焦点效果
    const inputs = document.querySelectorAll('input');
    inputs.forEach(input => {
        input.addEventListener('focus', function() {
            this.style.borderColor = '#667eea';
        });
        input.addEventListener('blur', function() {
            this.style.borderColor = '#ddd';
        });
    });
});