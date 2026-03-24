import api from '../../utils/api.js';
import util from '../../utils/util.js';
import constant from '../../utils/const.js';

Page({
    data: {
        user: null,
        MINIO_AVATAR: constant.MINIO_AVATAR,
        avatarFile: []
    },

    // 获取个人信息
    getInfo: function () {
        let that = this;
        let url = '/select/' + wx.getStorageSync("user").id;
        api.get('user', url).then(res => {
            that.setData({'user': res});
        }).catch(err => console.log(err));
    },

    // 上传用户头像
    uploadAvatar(ev) {
        const {file} = ev.detail;

        // 检查文件类型
        if (file.type !== 'image') {
            util.error('图片格式有误');
            return false;
        }

        // 检查文件大小
        if (file.size > 500 * 1024) {
            util.error('图片过大');
            return false;
        }

        // 上传文件
        wx.uploadFile({
            url: constant.UPLOAD_AVATAR_URL + wx.getStorageSync("user").id,
            filePath: file.url,
            name: 'avatarFile',
            header: {
                'Content-Type': 'multipart/form-data',
                'token': wx.getStorageSync('token')
            },
            success: (res) => {
                this.setData({avatarFile: res});
                util.success('上传成功');
                util.tab('/pages/user/user');
            },
            fail: (err) => console.log(err)
        });
    },

    // 退出登录
    logout: function () {
        util.confirm('即将退出登录，确认吗？', () => {
            wx.removeStorageSync('token');
            wx.removeStorageSync('user');
            util.page('/pages/login/login-by-account/login-by-account', false);
        });
    },

    // 注销账号
    remove: function () {
        util.confirm('即将注销账号，确认吗？', () => {
            let url = '/delete/' + wx.getStorageSync("user").id;
            api.del('user', url).then(res => {
                util.success('注销成功');
                util.page('/pages/login/login-by-account/login-by-account', false);
            }).catch(err => console.log(err));
        });
    },

    // 加载函数
    onLoad: function () {
        if (util.isLogin()) {
            this.getInfo();
        }
        this.getTabBar().setData({"activeTab": 3});
    },
});