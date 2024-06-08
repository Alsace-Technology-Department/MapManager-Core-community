package work.alsace.mapmanager.enums

enum class MapGroup {
    /**
     * 地图管理员
     * 拥有地图的最高权限，可以执行所有操作。
     */
    ADMIN,

    /**
     * 地图建筑师
     * 拥有地图的建筑权限，通过worldbase权限组配置。
     */
    BUILDER,

    /**
     * 地图访客
     * 仅有地图进入权限。
     */
    VISITOR
}
