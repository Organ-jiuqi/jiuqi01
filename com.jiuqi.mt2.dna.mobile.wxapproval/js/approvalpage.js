//初始化加载
window.onload = function () {
    hideWXMenu();
    disposeSplash();
    editField();
    clearexception();
    calculateWindowWidth();
}
//处理页面切换的闪屏
function disposeSplash() {
    $(document).bind("mobileinit", function () {
        if (navigator.userAgent.indexOf("Android") != -1) {
            $.mobile.defaultPageTransition = 'none';
            $.mobile.defaultDialogTransition = 'none';
        }
    });
}

//隐藏微信右上角目录
function hideWXMenu() {
    if (typeof WeixinJSBridge == 'undefined') {
        if (document.addEventListener) {
            document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
        } else if (document.attachEvent) {
            document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
            document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
        }
    } else {
        onBridgeReady();
    }
}
function onBridgeReady() {
    WeixinJSBridge.call('hideOptionMenu');
}
//按钮状态，0 可以提交 ；大于0，不可以提交
var button_flag = 0;

//计算窗口宽度
function calculateWindowWidth() {
    var width = document.body.offsetWidth;
    $('.billtable_td_left').css({'width': width * 0.35 + 'px'});
    $('.billtable_td_right').css({'width': width * 0.65 - 20 + 'px'});
}
//错误时执行
function onError(data, status) {
    button_flag = 0;
    $.mobile.loading('hide');
    $('#hint_yes').hide().unbind();
    $('#hint_no').hide().unbind();
    $('#hint_title').html('审批失败');
    $('#hint_context').html('返回结果有误,联系管理员!<br/>详细信息：ajax返回状态为error;或无返回值;或等待超时');
    $('#hint_yes').show().bind('click', function () {
        $.mobile.changePage('#home_page');
        hideWXMenu();
    });
    $.mobile.changePage('#hint_page');
    hideWXMenu();
}
//后面Action的请求
function trailingAction(url, action) {
    if (button_flag != 0) {
        return;
    }
    button_flag++;
    $.ajax({
        type: 'POST',
        url: url + action,
        cache: false,
        data: '',
        dataType: 'json',
        success: onSuccess,
        error: onError
    });
    $.mobile.loading('show', {text: '审批中...', textVisible: true, theme: 'b', textonly: false, html: ''});
}
//关闭页面
function closepage() {
    wx.closeWindow();
    window.opener = null;
    window.open('', '_self');
    window.close();
}
//审批意见提示
function commentHint(str) {
    $('#hint_page_back').show();
    $('#hint_yes').hide().unbind();
    $('#hint_no').hide().unbind();
    $.mobile.loading('hide');
    $('#hint_title').html('错误提示');
    $('#hint_context').html(str + '审批意见为必填项,不能为空!');
    $('#hint_yes').show().bind('click', function () {
        $.mobile.changePage('#dialog_page');
        hideWXMenu();
    });
    $.mobile.changePage('#hint_page');
    hideWXMenu();
}
//未执行关闭
function openorclose(id) {
    $('#open_msg').hide();
    $('#close_msg').hide();
    $('#hite_msg').hide();
    if (id == 1) {
        $('#close_msg').show();
        $('#hite_msg').show();
    } else if (id == 2) {
        $('#open_msg').show();
    }
}
//清理异常
function clearexception() {
    $('#open_msg').hide();
    $('#close_msg').hide();
    $('#hint_msg').html('');
}

//-----------------------------------
/*
//编辑字段
function editField() {
    for (var i = 0; i < feildid.length; i++) {
        var fname = feildid[i].replace('.', ' .');
        var ftype = $('#' + fname).attr('textType');
        if (ftype == 'string' || ftype == 'int' || ftype == 'numric' || ftype == 'date') {
            $('#' + fname).css({'border-bottom': '1px solid rgb(52,152,219)'}).attr('contenteditable', 'true');
            if ($('#' + fname).html().length > 10) {
                $('#' + fname).css({'display': 'inline', 'width': 'auto'});
            } else {
                $('#' + fname).css({'display': 'inline-block', 'width': '140px'});
            }
            if (ftype == 'date') {
                $('#' + fname).attr('onfocus', 'removeFInt(this);movetop(this)').attr('onblur', 'editData(this)').attr('onkeyup', 'removeFInt(this)').attr('onkeydown', 'updatestate(this)');
            } else if (ftype == 'int') {
                $('#' + fname).attr('onfocus', 'removeFInt(this);movetop(this)').attr('onkeyup', 'removeFInt(this)').attr('onkeydown', 'updatestate(this)').attr('onblur', 'removeFInt(this)');
            } else if (ftype == 'numric') {
                $('#' + fname).attr('onfocus', 'removeFNum(this);movetop(this)').attr('onkeyup', 'removeFNum(this)').attr('onkeydown', 'updatestate(this)').attr('onblur', 'onblurFNum(this)');
            } else if (ftype == 'string') {
                $('#' + fname).attr('onkeydown', 'updatestate(this)').attr('onfocus', 'movetop(this)');
            }
        } else {
            feildid.splice(i, 1);
            i--;
        }
    }
}
//编辑日期
function editData(td) {
    td.innerHTML = td.innerHTML.replace(/[^0-9]/g, '');
    if (td.innerHTML == '') {
        return true;
    }
    if (td.innerHTML != null && td.innerHTML.length == 8) {
        if (td.innerHTML.substr(0, 1) == 0) {
            alert('请输入有效年份!');
            return false;
        } else if (td.innerHTML.substr(4, 2) > 12 || td.innerHTML.substr(4, 2) == 0) {
            alert('请输入有效月份!');
            return false;
        }
        if (td.innerHTML.substr(6, 2) > 31 || td.innerHTML.substr(6, 2) == 0) {
            alert('请输入有效日期!');
            return false;
        } else {
            td.innerHTML = (td.innerHTML.substr(0, 4) + '-' + td.innerHTML.substr(4, 2) + '-' + td.innerHTML.substr(6, 2));
            return true;
        }
    } else {
        alert('日期应为8位有效数字');
        return false;
    }
}
//移除非整数
function removeFInt(td) {
	var judgeInt = /^([-]?)([0-9]+)$/;
	if(!judgeInt.test(td.innerHTML)){
		td.innerHTML = td.innerHTML.replace(/^([-]?)([0-9]+)/g, '');
	}
    
}
//移除非数字
function removeFNum(td) { 
	var value = td.innerHTML;
	var judgeDouble = /^([-]?)([0-9]+)([.]?)([0-9]*)$/;
	if(!judgeDouble.test(value)){
		 td.innerHTML = value.replace(/[^\d+\.]/g, '');
	}
    if (value.indexOf('.') > -1) {
        var arr = value.split('.');
        if (isNaN(parseInt(arr[0]))) {
            td.innerHTML = '0.' + arr[1].substr(0, 2);
        } else {
            td.innerHTML = parseInt(arr[0]) + '.' + arr[1].substr(0, 2);
        }
    }
}
//获取焦点
function onblurFNum(td) {
    removeFNum(td);
    td.innerHTML = parseFloat(td.innerHTML);
}
//页面向上移动
function movetop(ts) {
    document.body.scrollTop += (ts.getBoundingClientRect().top - 120);
}
//更新状态
function updatestate(ts) {
    if (ts.innerHTML.length > 10) {
        ts.style.display = 'inline';
        ts.style.width = 'auto';
        ts.style.border_bottom = '1px solid rgb(52,152,219)';
    } else {
        ts.style.display = 'inline-block';
        ts.style.width = '140px';
    }
}
//保存字段
function saveField() {
    var datas = '';
    var judgeDate = /^(\d{4})([-])(\d{2})([-])(\d{2})$/;
    //var judgeDate=/^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$/;
    var judgeInt = /^([-]?)([0-9]+)$/;
    var judgeDouble = /^([-]?)([0-9]+)([.]?)([0-9]*)$/;
    for (var i = 0; i < feildid.length; i++) {
        var fild = $('#' + feildid[i].replace('.', ' .'));
        for (var j = 0; j < fild.length; j++) {
            var ldata = fild.eq(j).text();
            var type = fild.eq(j).attr('TextType');
            var recid = fild.eq(j).attr('RECID');
            var bdata = fild.eq(j).attr('BData');
            if (ldata != '' && type == 'date' && !judgeDate.test(ldata)) {
                alert(ldata + ' 格式有误!日期请使用yyyy-MM-dd格式');
                fild.eq(j).focus();
                return;
            } else if (ldata != '' && type == 'int' && !judgeInt.test(ldata.replace(/\,/g, ''))) {
                ldata = ldata.replace(/\,/g, '');
                alert(ldata + ' 格式有误!请输入数字');
                fild.eq(j).focus();
                return;
            } else if (ldata != '' && type == 'numric' && !judgeDouble.test(ldata.replace(/\,/g, ''))) {
                ldata = ldata.replace(/\,/g, '');
                alert(ldata + ' 格式有误!请输入数字');
                fild.eq(j).focus();
                return;
            }
            if (ldata != null && ldata != undefined && ldata != '' && ldata != bdata) {
                datas += feildid[i] + ';.,;' + recid + ';,.;' + ldata + ';..;' + type + ';,,;';
            }
        }
    }
    $('#datas').val(datas);
    $.mobile.changePage('#dialog_page');
    hideWXMenu();
}
*/




