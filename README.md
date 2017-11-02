# configgen

配置生成工具，可认为是个生成只读object的object-relational mapping工具。

## 项目概况

* 通过配置外键，取值范围，使策划可以随时检测数据一致性
* 通过生成代码，使程序方便访问类型化数据，外键引用和单独一行，支持java,csharp,lua
* 另一个可变列模式，不再自动猜测补全schema的配置生成工具 https://github.com/pirunxi/cfggen

## 快速开始
* example/config 下是csv，config.xml 是配置文件，演示了ref，enum，nullableref，listref，bean的使用
* example/java, cs, lua目录下文件是gen.bat生成的代码文件。

## 使用流程

1. 新建或修改csv文件，csv文件前2行为header，第一行是desc，第二行是name
2. 使用configgen.jar 来生成或完善config.xml
3. 如果config.xml不满足需求，则手动修改config.xml，比如修改type，增加ref，enum，range
4. 重复1，2，3流程

## 配置描述

* bean.name
    - 文件路径，全小写，路径用.分割。这样和package名称统一，同时避免linux，windows差异
    - bean必须自己手工在config.xml里定义；column.type包含bean的时候也必须手工指定；这些没法自动猜测。
* bean.compress
    - 如果要把bean整个放到一个单元格里，配置一个分隔符，默认推荐用分号;
* bean.enumRef
    - 支持动态bean，这个指向一个enum的table，在这个bean里定义子bean
* table.name
* table.primaryKey
    - primary key! 默认是第一个field，如果不是请配置，逗号分割，比如keys="aa,bb"就是2个field aa,bb为key
* table.enum
    - 如果程序想访问单独一行，配置这个，比如enum="aa"就是field aa作为enum名称
* column.desc, name
    - configgen.jar会从csv文件第1,2行提取
    - list a1,a2: name为aList
    - map a1,b1,a2,b2: name为a2bMap
    - 生成代码时保留csv中配置名称的大小写，成员变量为首字母小写的name
    - 引用的成员变量为Ref+首字母大写的name
* column.type
    - bool,int,long,float,string(text),bean
    - list,xx,count     ArrayList；count不存在的时候，单元格是list；count存在的话从多列fieldname1，fieldname2。。。中读取数据。
    - map,xx,yy,count   LinkedHashMap; ;xx,yy都为基本格式。从多列fieldkey1，fieldvalue1，fieldkey2, fieldvalue2。。。中读取数据。

    - csv一个单元格,可以是基本类型，也可以是bean或者list，这时要配置compress分隔符
    - text用于客户端实现国际化需求，所有配置为text的字段数据会被单独放入一个文件中，只要修改这个文件就自动起作用了。
    - bean,list通过分号;进行分隔，例如a;b;c，转义规则同csv标准，比如数组里的一个字符串含有;，那么得用"号把它扩起来，如果引号里有引号，则要用双引号
        - "a";b;c   等同与a;b;c
        - "a;b";c   则被分为2组a;b 和c
        - "a"";b";c 也是2组a";b和c
    - map, lua不支持map的key为bean。
    - 如果type里包含bean，且不是一个单元格，则要在csv里第二行名字起名为field.name@xx，同时从这开始后列名字序列不要断，要和config.xml里的定义顺序一致，方便程序检测这个field的结束点。

* column.compress
    - 对list，count为0的时候，配置这个，用于分隔符
* column.ref, keyref
    - 引用，如果ref到主键只填table.name就行，如果ref到uniqueKey，则填table.name,column.name,逗号分割。keyref只针对map
* column.refType
    - 默认不用填为normal，如果nullable表示可为空，如果list，则不需要ref里的column不需要是unique key
* column.range
    - 对应min,max必须两者同时都有，对数值是取值区间，对字符串是长度区间。



* uniqueKey.keys
    - unique key!
* foreignKey.name, keys, ref, keyref, refType
    - keys 可是多列，逗号分割
* range.key, min, max
    - column.range 的另一种写法。
* table/bean/column.own
    - 里面可填任意字符串，配合启动参数使用，contains语义。
    - 共用一份config.xml，通过启动参数own和这里的own选择生成部分，想省客户端内存用这个

## 其他

*   有哪些注意事项？

      单元格中不填的话默认为false,0,""，所以不要用0作为一行的id。
      如果有nullableref请不要填0，请用留空。否则程序会检测报错

*   为什么使用csv？

      比xml简洁，可被版本管理系统diff，可用excel编辑。如果要用excel高级功能，请策划使用excel原始格式，导出csv

*   为什么支持enum？

      当有一个knowledge，客户端，服务器，策划都要了解的时候，放到csv里。程序也不用写魔数了。

*   为什么enum支持部分设置，部分空白？

      有时候允许部分设置是很方便的，比如掉落表，一般用id索引，但有些行如果能配置enum导出引用，则程序会方便的多。
      所以enum在实现上，java中如果部分enum则用静态成员，如果全部enum则生成enum；c#中生成为一个静态成员。

*   keys, ref是什么？

      keys对应于sql中的primary key，默认不用配置，就是第一列。
      ref对应于foreign key，如果是ref对应到那个table的primary key则只配置table就行，如果ref到uniq key，则配置table,uniqkey。
      ref不但用于策划数据一致性检测，也会直接生成代码，让程序员直接访问

*   为什么要支持nullableref？

      java，c#的引用可以为null，应该是个设计错误。简单点说原因就是允许为null妨碍了类型状态的最小化。后来java引入
      Optional就感觉是个nullableref的含义。idea引入@Nullable都是为了弥补这个。
      这里我们约定ref就是必须有引用的，nullableref是可为null的，生成代码时用前缀ref，nullableRef来做区别，逻辑使用refXx就不用检测是否为null了。
      参考：http://www.infoq.com/presentations/Null-References-The-Billion-Dollar-Mistake-Tony-Hoare

*   listref的使用场景？

      比如一般任务task，有个前置任务配置pretaskid，指的是这个任务完成前必须先完成这个前置任务。
      我需要知道当前任务完成后会开启哪些任务, 这时配置listref="task,pretaskid"。
      比如配置掉落表loot，然后lootitem是具体信息，loot里不用指明包含哪些lootitemid，而是在lootitem里指明lootid。
      这样再给lootid配上listref="lootitem,lootid"。

*   keyref的使用场景？

      只针对map，现在没用到，完整性上来说要应该有啊，特别是它的key是enum这种应该挺常见。

*   嵌套结构支持？

      可以在一个csv里直接随意嵌套bean；
      也可以通过ref,nullableref支持了简单嵌套结构，
      通过list,map，和ref，nullableref，keyref的使用支持了容器嵌套结构。
      还可以通过listref，支持来list嵌套结构。

*   继承支持？

      不支持，但有2中方法来处理：
    - 组合来做，定义一个bean。想继承的配置都使用这个bean。比如Fighter需要传cfg，但可能是monster，hero。
    - 使用csharp的partial机制，自己写个接口类，然后把想继承的配置类声明上，比如各种类型的item可放到不同的csv中。

*   客户端更新策略？

      分包处理，使用 -gen pack 配合pack.xml来生成分包文件。pack.xml里声明哪些文件打成一个包，使用CSVLoader.LoadPack来加载。
      如果pack.xml不存在，就全打包到all.zip。

*   国际化策略？

      对于需要国际化的字段，配置为text类型，生成时会单独放到text.csv里，只要翻译这个文件就ok了。
      翻译完毕使用 -packtext text.csv 来打包text.zip。

*   为什么java生成没有用public final？

      用public final int a; 这样就不用写getter了，简洁不少。
      但ref得用函数，enum类也得用函数，太不统一了。

*   对set的支持呢？

      不支持，这种配置不会太多，使用list效率够了。这个现在的想法是不支持。

*   待定

      C#版本加了个KeyedList用于对应java的LinkedHashMap，里面虽说有Generic，但对所有引用应该只生成一份代码，按说不会占用很多代码段，
      但不知道Mono是否这样？
