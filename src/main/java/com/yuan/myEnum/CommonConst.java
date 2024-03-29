package com.yuan.myEnum;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/27 0:44
 * @Description 对应缓存的键值
 */
public class CommonConst {

    /**
     * 超级管理员的用户Id
     */
    public static final int ADMIN_USER_ID = 1;

    /**
     * 根据用户ID获取Token
     */
    public static final String USER_TOKEN = "user_token_";

    public static final String ADMIN_TOKEN = "admin_token_";

    /**
     * 根据用户ID获取Token
     */
    public static final String USER_TOKEN_INTERVAL = "user_token_interval_";

    public static final String ADMIN_TOKEN_INTERVAL = "admin_token_interval_";

    /**
     * Token
     */
    public static final String USER_ACCESS_TOKEN = "user_access_token_";

    public static final String ADMIN_ACCESS_TOKEN = "admin_access_token_";
    /**
     * token的请求头
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * Token过期时间：1天
     */
    public static final long TOKEN_EXPIRE = 864000;

    /**
     * Token重设过期时间间隔：1小时
     */
    public static final long TOKEN_INTERVAL = 3600;

    /**
     * Boss信息
     */
    public static final String ADMIN = "admin";


    /**
     * 评论和IM邮件
     */
    public static final String COMMENT_IM_MAIL = "comment_im_mail_";

    /**
     * 评论和IM邮件发送次数
     */
    public static final int COMMENT_IM_MAIL_COUNT = 1;

    /**
     * 验证码
     */
    public static final String USER_CODE = "user_code_";

    /**
     * 忘记密码时获取验证码用于找回密码
     */
    public static final String FORGET_PASSWORD = "forget_password_";

    /**
     * 网站信息
     */
    public static final String WEB_INFO = "webInfo";

    /**
     * 分类信息
     */
    public static final String SORT_INFO = "sortInfo";

    /**
     * 密钥
     */
    public static final String CRYPOTJS_KEY = "aoligeimeimaobin";

    /**
     * 根据用户ID获取用户信息
     */
    public static final String USER_CACHE = "user_";

    /**
     * 根据文章ID获取评论数量
     */
    public static final String COMMENT_COUNT_CACHE = "comment_count_";

    /**
     * 根据用户ID获取该用户所有文章ID
     */
    public static final String USER_ARTICLE_LIST = "user_article_list_";

    /**
     * 默认缓存过期时间
     */
    public static final long EXPIRE = 1800;

    /**
     * 树洞一次最多查询条数
     */
    public static final int TREE_HOLE_COUNT = 200;

    /**
     * 顶层评论ID
     */
    public static final int FIRST_COMMENT = 0;

    /**
     * 文章摘要默认字数
     */
    public static final int SUMMARY = 80;


    /**
     * 七牛云
     */
    public static final String ACCESS_KEY = "MgO-SncVeUFRzH-n8LrzeL1O2B6vYd-ZCggEWTrW";

    public static final String SECRET_KEY = "otpPxPsE41zj5pKnapjoY8O72SCYIPFYviP24haW";

    public static final String BUCKET = "pppyyy";

    public static final String DOWNLOAD_URL = "starquake.cn";

    /**
     * 资源类型
     */
    public static final String PATH_TYPE_GRAFFITI = "graffiti";

    public static final String PATH_TYPE_ARTICLE_PICTURE = "articlePicture";

    public static final String PATH_TYPE_USER_AVATAR = "userAvatar";

    public static final String PATH_TYPE_ARTICLE_COVER = "articleCover";

    public static final String PATH_TYPE_WEB_BACKGROUND_IMAGE = "webBackgroundImage";

    public static final String PATH_TYPE_WEB_AVATAR = "webAvatar";

    public static final String PATH_TYPE_RANDOM_AVATAR = "randomAvatar";

    public static final String PATH_TYPE_RANDOM_COVER = "randomCover";

    public static final String PATH_TYPE_COMMENT_PICTURE = "commentPicture";

    public static final String PATH_TYPE_INTERNET_MEME = "internetMeme";


    /**
     * 资源路径
     */
    public static final String RESOURCE_PATH_TYPE_FRIEND = "friendUrl";
    public static final String RESOURCE_PATH_TYPE_FAVORITES = "favoritesUrl";


    /**
     * 微言
     */
    public static final String WEIYAN_TYPE_FRIEND = "friend";

    /**
     * 缓存设计
     */
    //文章
    public static final String ARTICLE_CACHE="article_cache:";
    //标签
    public static final String SORT_CACHE="sort_cache:";
    //评论
    public static final String Label_CACHE="label_cache:";

    /**
     * Token过期时间：1天
     */
    public static final long CACHE_EXPIRE = 4*60*60;


    /**
     * 临时文件 前缀key
     */
    public static final String TEMP_FILE_PREFIX = "temp_key:";
    /**
     * 临时文件过期 延时队列
     */
    public static final String DELAYED_DOC_TASKS="delayed_doc_tasks";

    /**
     * 临时文件夹名称
     */
   // public static final String TEMP_PIC_DIR_PATh = "./temp/picture/";
    public static final String TEMP_PIC_DIR_PATh = ".\\temp\\picture\\";

}
