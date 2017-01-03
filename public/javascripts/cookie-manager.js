function setCookie(cname,cvalue,expiredays) {
    var exp  = new Date();
    exp.setTime(exp.getTime() + expiredays*24*60*60*1000);
    document.cookie = cname + "="+ escape(cvalue) + ";expires=" + exp.toGMTString()+";path="+"/";
}

function clearCookie(name) {  
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    document.cookie= name + "=''"+";expires="+exp.toGMTString()+";path="+"/";
}  

function adminLogout() {
    clearCookie('wechat_id');
    clearCookie('token');
    clearCookie('screen_name');
    clearCookie('screen_photo');
    location.href = '/admin/login';
}

function logout() {
	clearCookie('wechat_id');
    clearCookie('token');
    clearCookie('screen_name');
    clearCookie('screen_photo');
    location.href = '/';
}