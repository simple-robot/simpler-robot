# common-configuration

提供配置读取功能模块。


ConfigurationManagerBuilder --> 
ConfigurationManager --`.parse(type, resources)`-->  
Configuration --`.getConfig(key)`--> 
ConfigurationProperty --`getObject(type)`--> config you need.

or 

ConfigurationManagerBuilder --> 
ConfigurationManager --`.parse(type, resources)`-->  
Configuration --`.getConfig(key)` --|
ConfigurationInjector --`inject(configInstance, Configuration, ConverterManager)`--> inject config.



## 获取ConfigurationManager
```
// 通过实现一个builder构建
ConfigurationManagerBulider.build()
```

```
// 一个默认的manager
ConfigurationManagerRegistry.defaultManager()
```
