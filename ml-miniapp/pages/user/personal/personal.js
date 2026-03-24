import api from '../../../utils/api.js';
import util from '../../../utils/util.js';
import constant from '../../../utils/const.js';

Page({
    data: {
        user: null,
        MINIO_AVATAR: constant.MINIO_AVATAR,
    },

    // 获取个人信息
    getInfo: function () {
        let that = this;
        let url = '/select/' + wx.getStorageSync("user").id;
        api.get('user', url).then(res => {
            res['updated'] = util.dateFormat(res['updated']);
            res['created'] = util.dateFormat(res['created']);
            that.setData({'user': res});
        }).catch(err => console.error(err));
    },

    onLoad: function (options) {
        if (util.isLogin()) {
            this.getInfo();
        }
    }
});