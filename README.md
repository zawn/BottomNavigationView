# BottomNavigationView

Fragment instance reuse for BottomNavigationView

BottomNavigationView fragment实例复用

## Android Studio生成的Demo效果演示

以下为按照Android Studio生成的Demo使用的效果,可以看到多次点击Home所在的tab标签,重复创建了多个home fragment的实例, 当然重复点击任意个tab标签都会重新创建实例, 演示的最后为使用返回键返回的效果,可以发现从home(996a089)切换到了其他标签页,在使用返回键返回,可以回到之前的home(996a089),也就是说其实home(996a089)的实例在离开当前标签页的时候并未销毁.

### 原因分析
FragmentNavigator是基于FragmentManager返回堆栈实现的,实际上FragmentNavigator也可以实现实例复用,但是FragmentManager返回堆栈是无法修改的,所以只有按返回键的时候才有复用, 在BottomNavigationView切换页面的时候也就无法调用返回堆栈中的实例,只能销毁堆栈中的实例重新创建.

![enter image description here](https://github.com/zawn/BottomNavigationView/blob/demo/gif/SVID_20210802_114205_1.gif?raw=true)

## 解决方法

本来不想花时间研究这些的,就在网上找了一个实现,[Navigation 之 Fragment 切换 | Jiang (jiangjiwei.site)](https://jiangjiwei.site/post/navigation-zhi-fragment-qie-huan/)这个文章写的比较长, 有兴趣可以看看.

基本思路是合理的保存已经创建的fragment实例,在需要的时候重用之前的实例.但这里需要提醒一下,原始的FragmentNavigator并不是没有保持已经创建的fragment实例,在多页面场景下FragmentNavigator的实现可以做到重用之前的实例,但是FragmentNavigator的实现是基于FragmentManager返回栈的,而FragmentManager返回栈中的实例是无法随意获取的,你只能一个个出栈,一个进站,目前你无法做到像`android:launchMode="singleInstance"`一样效果.

回到上面的Tab页切换场景,我们希望Home,Dashboard,Notifications的fragment页面是singleInstance的,但是基于返回栈的FragmentNavigator无法做到,如果当前堆栈为Home>Dashboard>Notifications,那么回到Home可以通过出栈操作实现,但是这时候就会丢失Notifications,Dashboard的实例(注: 上面的例子中实际堆栈为Home>Notifications,或者Home>Dashboard,因为`NavigationUI.onNavDestinationSelected`中做了特殊的`builder.setPopUpTo(findStartDestination(navController.getGraph()).getId(), false);`操作),

关于[Navigation 之 Fragment 切换 | Jiang (jiangjiwei.site)](https://jiangjiwei.site/post/navigation-zhi-fragment-qie-huan/)中的实现其实是有些问题的,这个大家可以自己尝试下,这里仅说一下它的使用场景,他仅适用Tab页面切换的场景,其他的请使用官方的FragmentNavigator, FragmentNavigator也不会丢失实例状态的,放心吧,


## 实现效果
![enter image description here](https://github.com/zawn/BottomNavigationView/blob/master/gif/SVID_20210802_132650_1.gif?raw=true)


演示的手机我设置了不保留Activity,在返回桌面后Activity会被销毁,大家可以看到,销毁重建后fragment之前的state值依然存在. 收工

最终实现代码在:
[BottomNavigationView/FragmentTabNavigator.java at master · zawn/BottomNavigationView (github.com)](https://github.com/zawn/BottomNavigationView/blob/master/app/src/main/java/com/saicmotor/sc/myapplication/ui/FragmentTabNavigator.java)

### 使用方法

1.	更改navigation的xml文件将`<fragment`标签换为`<fragment_tab`如下:
2.	更改activity中的fragment定义去除对navigation xml文件的引用
3.	参照MainActivity文件更改使用方法,
