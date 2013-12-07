bbks-crawer
===========

E-reading platform(数据榨取集成部分)		

##简介		

bbks是基于图书搜索的电子阅读、社交平台。系统分为：图书搜索系统，社交互动系统以及电子阅读系统。		
其中图书搜索系统分为：数据抓取系统、数据集成系统和图书检索平台。该应用采用Appache开源协议。

##	技术	

	###	backend			
	*	SpringMVC freamwork
	* 	SpringData
	* 	Hibernate3.x
	*	Lunce+CK
	*	Appache shiro
	* 	encache		

##	代码说明
	###	Note：在阅读下面的内容时，请详细阅读系统接口设计文档，以及数据库设计文档。
	
	###	集成系统代码说明	
	
		集成系统的代码，分布在	
		
			package com.bimoku.integrate;
		实现思路：	
		
		集成系统代码，使用了工厂，模版以及控制反转的设计思路。整个系统提供一个接口，即：Integrated这个方法。该方法详细情况：	
		
			/**
				 * 集成接口
				 * @param detail
				 * @throws IntegratedException
				 */
			public void integrated(BookDetail detail) 
			throws IntegratedException
		集成系统的代码整体上比较简单，主要就是在抽取结束后，明细数据入库时，执行这部分代码。	
		
		这部分代码主要处理了：去重，字段取优，关系映射。等。		
		达到的目的是：通过集成数据表t_book可以独到分库（分库的数据表）数据。
		一遍使用join，但是数据量大的时候，还是尽量把联合的操作放在代码中，就
		是通过集成表的查询，得到分表的主键（或其他索引键），然后在分表（单表）进行查询。				
		![DataV logo](https://raw.github.com/cncduLee/bbks-crawer/master/doc/p1.png)
		
		###	外部集成		
		
			![DataV logo](https://raw.github.com/cncduLee/bbks-crawer/master/doc/p2.png) 
			
			集成接口1：（通用接口）
			![DataV logo](https://raw.github.com/cncduLee/bbks-crawer/master/doc/p3.png) 
			
			集成接口2：（实时数据集成）		
			![DataV logo](https://raw.github.com/cncduLee/bbks-crawer/master/doc/p4.png)
			 
			集成接口3：（通用接口）		
			![DataV logo](https://raw.github.com/cncduLee/bbks-crawer/master/doc/p5.png)
			
			集成接口：（内部集成）		
			![DataV logo](https://raw.github.com/cncduLee/bbks-crawer/master/doc/p6.png)
					
			![DataV logo](https://raw.github.com/cncduLee/bbks-crawer/master/doc/p7.png)
		
		###	使用方法：		
		
		使用代码在test目录下，可以参看测试代码。测试代码说明了集成接口的调用方式。
		![DataV logo](https://raw.github.com/cncduLee/bbks-crawer/master/doc/p8.png)
	###	抽取系统代码说明		
	
	抽取代码设计思路是，通过配置文件以及抽取规则的编码就可以根据不同站点，不同内容做相关的抽取工作。
	 ![DataV logo](https://raw.github.com/cncduLee/bbks-crawer/master/doc/p9.png)
	
	抽取文件的详细说明：
	![DataV logo](https://raw.github.com/cncduLee/bbks-crawer/master/doc/p10.png)
	要运行抽取代码，只需要从Main这个文件开始扫描，里面就有说明。		
	![DataV logo](https://raw.github.com/cncduLee/bbks-crawer/master/doc/p11.png)
##	进度		
		
该部分已经初步完成
