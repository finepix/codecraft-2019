# codecraft-2019
写不出判题器，跟着调参当混子。初赛用的python，效率太低，花了三四天时间将代码移植到java.复赛4500左右，仅仅调到第二步，理论上能调到3500左右，没时间了。

# 调参策略
总体希望在路上跑的车越多越好，且总的时间最短。（每分配一辆车，改变对应路径的权重）  
1、将车辆分类：预置车辆（发车时间顺序排序），非预置车辆（a.优先级，b.速度）；  
2、调大每次发车数量，同时缩短时间间隔，直至卡死；  
3、按分段平均速度分配时间间隔，直至卡死；  
4、3中卡死，根据官方判题器给定的阻塞点，使用加大路径权重（直至inf）。
