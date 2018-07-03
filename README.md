演示图片
--------
![image](https://github.com/bouquet12138/pictureLibrary/blob/master/dateSelected.gif)
<br>
简单使用
--------	
	项目是由三个DatePickerView 组成的 
	为每个DatePickerView设置数据的方法 对象.setDateList(List<String> );
	<br>
	
DatePickerView 属性介绍
-------------
	 * data1Color 被选中文本的颜色 默认为 0xffdda4e4
	 * data2Color 未选中数据的颜色 默认为 0xff444444
     * canScroll  是否可以滚动     默认可以滚动
	 <br>
	 
BottomPopDateSelect 介绍
--------------
	BottomPopDateSelect 其实就是3个 DatePickerView 加底部弹窗的组合 具体大家可以自行设置
	bottomPopDateSelect.setOnConfirmListener(new OnConfirmListener()) 
	上面的可以设置完成按钮的点击监听 会将日期以 2017-07-23的形式返回出去
	具体看代码吧
	