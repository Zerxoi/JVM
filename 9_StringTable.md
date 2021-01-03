# StringTable

## String 的基本特性

字符串使用一对`"`来表示.

```java
String s1 = "i am a string";
String s2 = new String("i am a string too");
```

String声明为`final`, 不可被继承

实现了`Serializable`接口: 表示字符串是支持序列化的.

实现了`Comparable`接口: 表示字符串是可以比较的.

---

**字符串内部实现的改变**

String在JDK 8 及以前内部定义了 `private final char[] value` 用于存储字符串类型; JDK 9 时改为`private final byte[] value`.

`char`类型数据占两个字节. 

- 字符串是堆使用的主要部分
- 字符串对象主要包含的是`Latin-1`字符, 这种字符仅需要一个字节的存储空间, 因此会浪费一般的`char`类型空间

因此将`String`的内部实现UTF-16位的`char`数组变成了一个**添加编码标志(encoding-flag)字段的字节数组**. 如果是 ISO-8859-1/Lation-1 编码的字符使用一个字节存储; 其他字符采用 UTF-16存储(一个字符两个字节). 编码标志用于指示所使用的编码.

---

字符串代表不可变的字符序列, 即具有**不可变性**.

- 当对字符串重新赋值时, 需要重写指定内存区域赋值, 不能使用原有的`value`进行赋值.
- 当对现有的字符串进行连接操作时, 也需要重新指定内存区域赋值, 不能使用原有的`value`进行赋值.
- 当调用`String`的`replace`方法修改指定的字符或者字符串时, 也需要重新指定内存区域赋值, 不能使用原有的`value`进行赋值.

通过**字面量**的方式(区别于`new`)给一个字符串赋值, 此时的字符串值声明在**字符串常量池**中.

---

**字符串常量池是不会存储相同内容的字符串的**

String的String Pool是一个固定大小的Hashtable, 默认值大小长度是1009. 如果放进String Pool的String非常多, 就会造成Hash冲突严重, 从而导致链表会很长, 而链表长了后会直接影响`String.intern`的性能.

使用`-XX:StringTableSize`可设置StringTable的长度

在JDK 6中StringTable是固定的, 就是1009, 如果常量池中的字符串过多就会导致效率下降很快. `StringTableSize`的设置没有要求.

在JDK 7中, StringTable的默认长度是60013, `StringTableSize`的设置没有要求.

在JDK 8中, 1009是可设置的最小值.

`string.intern`: 如果字符串常量池中没有对应`string`字符串的话, 则会在常量池中生成; 如果有的话, 则直接返回字符串.

## String 内存分配

在Java语言中有8种数据类型和一种比较特殊的类型String, 这些类型为了使他们在运行过程中速度更快, 更节省内存, 都提供了一种常量池的概念.

常量池就类似一个Java系统级别提供的缓存. 8种基本数据类型的常量都是系统协调的, String类型的常量池比较特殊. 它的主要使用方法有两种:

- 直接使用双引号声明出来的String对象会直接存储在常量池中
- 如果不是用双引号声明的String对象, 可以使用String提供的`intern()`方法.

---

- Java 6及之前, 字符串常量池存放在永久代
- Java 7中Oracle的工程师对字符串常量池的逻辑做了很大的改变, 即将字符串常量池的位置调整到Java堆内.
    - 所有字符串都保存在堆(Heap)中, 和其他普通对象一样, 这样在调优的时候仅需调整堆的大小就可以了
    - 字符串常量池概念原本使用得比较多, 但是这个改动使得我们有足够的理由让我们重新考虑在Java 7中使用`String.intern`
- Java 8及之后, 方法区使用元空间来实现, 字符串常量池仍旧在堆中.

**为什么要把StringTable调整到永久代?**

1. PermSize默认比较小
2. 永久代垃圾回收频率低

## String的基本操作

Java语言规范里要求完全相同的字符串字面量, 应该包含相同的Unicode字符串序列(包含同一份码点序列的常量), 并且必须是指向同一个String类实例.

```java
public class Test {
    public static void main(String[] args) {
        String s1 = "Hello World!";
        String s2 = "Hello World!";

        System.out.println(s1 == s2);
    }
}
```

## 字符串的拼接

1. 常量(包括`final`)之间的拼接结果在常量池中, 原理是编译期优化.
2. 常量池中不会存在相同内容的常量.
3. 只要其中有一个是变量(不包括`final`), 结果就在堆中. 变量拼接的原理是`StringBuilder`, 相当于在堆空间中`new String()`.
4. 如果拼接的结果调用`intern`方法, 则主动将常量池中还没有的字符串对象放入池中, 并返回此对象地址; 如果常量池中存在, 则会返回常量池中相同字符串的地址.

```java
public class StaticField {
    public static void main(String[] args) {
        String s0 = "a";
        String s1 = "b";
        String s2 = "ab";
        String s3 = "a" + "b";
        String s4 = s0 + "b";
        String s5 = "a" + s1;
        String s6 = s0 + s1;
        String s7 = s6.intern();

        // s3 在编译期间拼接， 效果等同于 s2
        System.out.println(s2 == s3); // true

        // 带变量的拼接使用的是StringBuilder， 等同于new String对象
        System.out.println(s2 == s4); // false
        System.out.println(s2 == s5); // false
        System.out.println(s2 == s6); // false

        // 堆中new String创建的对象互不相等
        System.out.println(s4 == s5); // false
        System.out.println(s5 == s6); // false
        System.out.println(s4 == s6); // false

        // intern方法在字符串常量池中寻找和"ab"内容相同的字符串
        // 因为之前s2是字面量生成的字符串, 所以会放入常量池中
        // intern方法返回的结果就是常量池中的"ab", 所以和s2相等
        System.out.println(s2 == s7); // true
    }
}
```

**`final`常量的拼接**

`final`常量引用的拼接也是在编译期间就拼接好的

```java
final String s1 = "a";
final String s2 = "b";
String s3 = "ab"
String s4 = s1 + s2;
System.out.println(s3 == s4); // true
```

---

**含变量的字符串拼接底层实现**

```java
String s1 = "a";
String s2 = "b";
String s3 = s1 + s2;
```

上面语句的底层实现如下

```java
String s1 = "a";
String s2 = "b";
StringBuilder sb = new StringBuilder();
sb.append(s1).append(s2);
String s3 = sb.toString(); // 约等于 new String(), 因为没有ldc指令
```

## `intern`的使用

如果不适用双引号声明的`String`对象, 可以使用`String`提供的`intern`方法: `intern`方法会在字符串常量池中查询当前字符串是否存在, 若不存在就会将当前字符串放入常量池中; 若存在就会把字符串常量池中的字符串的引用返回.

也就是说, 如果在任意字符串上调用`String.intern`方法, 那么其返回结果所指向的那个类实例, 必会和直接以常量形式出现的字符串实例完全相同.

通俗点讲, Interned String就是确保字符串在内存里只有一份拷贝, 这样可以节约内存空间, 加快字符串操作任务的执行速度. 注意, 这个值会被存放在字符串内部池(String Intern Pool).

**如何保证变量是指向的是字符串常量池中的数据呢?**

```java
String s1 = "Hello";
String s2 = new String("Hi").intern();
```

---

**面试题**

`ldc`字节码指令在遇到`String`类型常量时, 如果发现StringTable已经有了内容匹配的`java.lang.String`的引用, 则直接返回这个引用; 反之, 如果StringTable里尚未有内容匹配的`String`实例的引用, 则会在Java堆里创建一个对应内容的`String`对象, 然后在StringTable记录下这个引用, 并返回这个引用出去.

题目: `String str = new String("ab");` 会创建几个对象?

答案: 2个

字节码指令如下
```
0: new           #2                  // class java/lang/String
3: dup
4: ldc           #3                  // String ab
6: invokespecial #4                  // Method java/lang/String."<init>":(Ljava/lang/String;)V      
9: astore_1
10: return
```

1. 通过`new`指令在堆空间中创建的对象
2. 使用`ldc`指令时常量池中并没有内容匹配的字符串, 因此会在堆空间中创建一个对象, 并在StringTable中记录其引用

拓展: `String str = new String("a") + new String("b");` 会创建几个对象?

答案: 5个

字节码指令
```
0: new           #2                  // class java/lang/String
3: dup
4: ldc           #3                  // String a
6: invokespecial #4                  // Method java/lang/String."<init>":(Ljava/lang/String;)V      
9: new           #2                  // class java/lang/String
12: dup
13: ldc           #5                  // String b
15: invokespecial #4                  // Method java/lang/String."<init>":(Ljava/lang/String;)V      
18: invokedynamic #6,  0              // InvokeDynamic #0:makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
23: astore_1
24: return
```

1. `new`指令在堆空间中创建的`"a"`对象
2. `ldc`指令在堆空间创建`"a"`对象, 并记录到常量池
3. `new`指令在堆空间中创建的`"b"`对象
4. `ldc`指令在堆空间创建`"b"`对象, 并在常量池中添加引用
5. `invokedynamic` 连接两个字符串对象在堆中生成一个新的字符串

题目: JDK 6↓ / JDK 7↑ 的 `intern`

```java
public class Test {
    public static void main(String[] args) {
        String s1 = new String("1");
        s1.intern();
        String s2 = "1";
        System.out.println(s1 == s2); // JDK 6↓: false   JDK 7↑: false

        String s3 = new String("1") + new String("1");
        s3.intern();
        String s4 = "11";
        System.out.println(s3 == s4); // JDK 6↓: false   JDK 7↑: true
    }
}
```

- JDK 6及之前: 字符串常量池在**永久代**中, 字符串常量池中保存的是永久代字符串对象的引用
- JDK 7及之后: 字符串常量池在**堆空间**中, 字符串常量池中保存的是堆空间字符串对象的引用

`s3` 的字符串内容为 `"11"`, 且并不存在于字符串常量池之中.

在JDK 6及之前, `s3.intern` 方法在字符串常量池中未找到和 `s3` 内容相匹配的字符串, 会在永久代中创建一个内容和`s3`一样的对象, 因此会将**以字符串内容和方法区中字符串地址为键值对添加到StringTable中**. 所以`s4`是永久代堆中的对象.

在JDK 7及之后, `s3.intern` 方法在字符串常量池中同样没找到和 `s3` 内容相匹配的字符串, 则直接将**字符串的内容和堆空间字符串`s3`的地址为键值对添加StringTable中**, 而无需额外创建对象. 所以`s4`是堆中的对象且和`s3`为同一对象.

**练习题**

```java
public class Test {
    public static void main(String[] args) {
        String s1 = new String("a") + new String("b");
        String s2 = s1.intern();

        System.out.println(s1 == "ab"); // JDK 6↓: false    JDK 7↑: true
        System.out.println(s2 == "ab"); // JDK 6↓: true     JDK 7↑: true
    }
}
```

`s1`是在堆中创建的字符串对象`"ab"`, 但是并不存在于字符串常量池.

在JDK 6中, 因为字符串常量池并没有和`s1`内容相匹配的字符串, 所以`s1.intern`方法会在永久代创建一个和`s1`内容一致的对象并添加到字符串常量池中, 将永久代中的字符串引用赋值给`s2`. 所以 `s1 == s2` 为 `false`.

在JDK 7中, 因为字符串常量池并没有和 `s1` 内容相匹配的字符串, `s1.intern` 方法会直接将 `s1` 的引用添加至字符串常量池中, 将该引用返回给 `s2`, 所以 `s1==s2` 为 `true`.

`s1.intern`返回常量池中相同字符串(即`"ab"`)的引用; `"ab"`字面量返回的是常量池中`"ab"`字符串的引用, 所以`s2 == "ab"` 无论在哪个版本都是`true`.

## `intern`的效率测试

```java
public class PCRegister {
    static final int MAX_COUNT = 10_000_000;
    static final String[] arr = new String[MAX_COUNT];

    public static void main(String[] args) {
        Integer[] data = new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        long begin = System.currentTimeMillis();
        for (int i = 0; i < MAX_COUNT; i++) {
            // arr[i] = String.valueOf(data[i % data.length]);
            arr[i] = String.valueOf(data[i % data.length]).intern();
        }
        long end = System.currentTimeMillis();
        System.out.println(end - begin);
    }
}
```
如果有大量的重复的字符串对象的话, 可以使用`intern`方法, 重复的字符串对象只会保存一份在字符串常量中, 堆中的其他重复的字符串对象会被垃圾回收, 以提高空间效率.

使用`-XX:PrintStringTableStatistics`打印字符串常量池的统计信息

StringTable也是存在垃圾回收行为的
