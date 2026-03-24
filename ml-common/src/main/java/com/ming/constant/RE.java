package com.ming.constant;

/**
 * @author JoeZhou
 */
public interface RE {
    String TITLE_RE = "^.{1,42}$";
    String TITLE_RE_MSG = "标题长度必须在1~42之间";
    String AUTHOR_RE = "^.{1,42}$";
    String AUTHOR_RE_MSG = "作者名称长度必须在1~42之间";
    String CODE_RE = "^.{1,42}$";
    String CODE_RE_MSG = "兑换口令长度必须在1~42之间";
    String SN_RE = "^.{1,42}$";
    String SN_RE_MSG = "订单编号长度必须在1~42之间";
    String INFO_RE = "^.{1,170}$";
    String INFO_RE_MSG = "描述长度必须在1~170之间";
    String CONTENT_RE = "^.{1,170}$";
    String CONTENT_RE_MSG = "内容长度必须在1~170之间";
    String MENU_URL_RE = "^/[a-zA-Z]{0,256}$";
    String MENU_URL_RE_MSG = "跳转地址必须以 / 开头，后续内容仅支持0~256个英文字母";
    String MENU_ICON_RE = "^[a-zA-Z]{1,256}$";
    String MENU_ICON_RE_MSG = "图标仅支持1~256个英文字母";
    String USERNAME_RE = "^[a-zA-Z0-9]{4,20}$";
    String USERNAME_RE_MSG = "账号必须由4到20个英文字母或数字组成";
    String NICKNAME_RE = "^[\\u4e00-\\u9fa5_a-zA-Z0-9]{4,10}$";
    String NICKNAME_RE_MSG = "昵称必须由4到10个中文，英文字母，数字或下划线组成";
    String PASSWORD_RE = "^[a-zA-Z0-9]{4,20}$";
    String PASSWORD_RE_MSG = "密码必须由4到20个英文字母或数字组成";
    String REALNAME_RE = "^[\\u4e00-\\u9fa5]{2,6}$";
    String REALNAME_RE_MSG = "真实姓名必须由2到6个中文组成";
    String ID_CARD_RE = "^[1-9]\\d{5}(19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
    String ID_CARD_RE_MSG = "身份证号格式不正确";
    String PHONE_RE = "^1(3[0-9]|4[01456879]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[0-35-9])\\d{8}$";
    String PHONE_RE_MSG = "手机号码格式不正确";
    String EMAIL_RE = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    String EMAIL_RE_MSG = "电子邮箱格式不正确";
    String ZODIAC_RE = "^[\\u4e00-\\u9fa5]{2,4}$";
    String ZODIAC_RE_MSG = "星座必须由2到4个中文组成";
    String PROVINCE_RE = "^[\\u4e00-\\u9fa5]{2,20}$";
    String PROVINCE_RE_MSG = "省份必须由2到20个中文组成";
    String VCODE_RE = "^\\d{6}$";
    String VCODE_RE_MSG = "验证码必须为6位数字";
}
