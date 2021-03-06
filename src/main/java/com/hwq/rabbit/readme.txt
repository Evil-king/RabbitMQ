 work queues 工作队列
 为什么会出现 work queues?
 前提:使用 simple 队列的时候
 我们应用程序在是使用消息系统的时候,一般生产者 P 生产消息是毫不费力的(发送消息即可),而消费者接收完消息
 后的需要处理,会耗费一定的时间,这时候,就有可能导致很多消息堆积在队列里面,一个消费者有可能不够用
 那么怎么让消费者同事处理多个消息呢?
 在同一个队列上创建多个消费者,让他们相互竞争,这样消费者就可以同时处理多条消息了
 使用任务队列的优点之一就是可以轻易的并行工作。如果我们积压了好多工作，我们可以通过增加工作者（消费者）
 来解决这一问题，使得系统的伸缩性更加容易

 Fair dispatch（公平分发）
 虽然上面的分配法方式也还行，但是有个问题就是：比如：现在有 2 个消费者，所有的偶数的消息都是繁忙的，而
 奇数则是轻松的。按照轮询的方式，偶数的任务交给了第一个消费者，所以一直在忙个不停。奇数的任务交给另一
 个消费者，则立即完成任务，然后闲得不行。
 而 RabbitMQ 则是不了解这些的。他是不知道你消费者的消费能力的,这是因为当消息进入队列，RabbitMQ 就会分派
 消息。而 rabbitmq 只是盲目的将消息轮询的发给消费者。你一个我一个的这样发送.
 为了解决这个问题，我们使用 basicQos( prefetchCount = 1)方法，来限制 RabbitMQ 只发不超过 1 条的消息给同
 一个消费者。当消息处理完毕后，有了反馈 ack，才会进行第二次发送。(也就是说需要手动反馈给 Rabbitmq )
 还有一点需要注意，使用公平分发，必须关闭自动应答，改为手动应答。


 Message acknowledgment（消息应答）
1. boolean autoAck = false;
2. channel.basicConsume(QUEUE_NAME, autoAck, consumer);
 boolean autoAck = true;(自动确认模式)一旦 RabbitMQ 将消息分发给了消费者，就会从内存中删除。
在这种情况下，如果杀死正在执行任务的消费者，会丢失正在处理的消息，也会丢失已经分发给这个消
费者但尚未处理的消息。
 boolean autoAck = false; (手动确认模式) 我们不想丢失任何任务，如果有一个消费者挂掉了，那么
我们应该将分发给它的任务交付给另一个消费者去处理。 为了确保消息不会丢失，RabbitMQ 支持消
息应答。消费者发送一个消息应答，告诉 RabbitMQ 这个消息已经接收并且处理完毕了。RabbitMQ 可
以删除它了。
 消息应答是默认打开的。也就是 boolean autoAck =false;

 Message durability（消息持久化）
我们已经了解了如何确保即使消费者死亡，任务也不会丢失。但是如果 RabbitMQ 服务器停止，我们的任务
仍将失去！当 RabbitMQ 退出或者崩溃，将会丢失队列和消息。除非你不要队列和消息。两件事儿必须保证消息不
被丢失：我们必须把“队列”和“消息”设为持久化。
1. boolean durable = true;
2. channel.queueDeclare("test_queue_work", durable, false, false, null);
那么我们直接将程序里面的 false 改成 true 就行了?? 不可以
会 报异常 channel error; protocol method: #method<channel.close>(reply-code=406, replytext=PRECONDITION_FAILED - inequivalent arg 'durable' for queue 'test_queue_work'
 尽管这行代码是正确的，他不会运行成功。因为我们已经定义了一个名叫 test_queue_work 的未持久化的队
列。RabbitMQ 不允许使用不同的参数设定重新定义已经存在的队列，并且会返回一个错误。
一个快速的解决方案——就是声明一个不同名字的队列，比如 task_queue。或者我们登录控制台将队列删除就可
以了

  订阅模式 Publish/Subscribe（在rabbitmq中，exchange有4个类型：direct，topic，fanout，header。）
我们之前学习的都是一个消息只能被一个消费者消费,那么如果我想发一个消息 能被多个消费者消费,这时候怎么
办? 这时候我们就得用到了消息中的发布订阅模型
在前面的教程中，我们创建了一个工作队列，都是一个任务只交给一个消费者。
这次我们做 将消息发送给多个消费者。这种模式叫做“发布/订阅”。
举列:
类似微信订阅号 发布文章消息 就可以广播给所有的接收者。(订阅者)
那么咱们来看一下图,我们学过前两种有一些不一样,work 模式 是不是同一个队列 多个消费者,而 ps 这种模式呢,是
一个队列对应一个消费者,pb 模式还多了一个 X(交换机 转发器) ,这时候我们要获取消息 就需要队列绑定到交换机
上,交换机把消息发送到队列 , 消费者才能获取队列的消息
解读：
1、1 个生产者，多个消费者
2、每一个消费者都有自己的一个队列
3、生产者没有将消息直接发送到队列，而是发送到了交换机(转发器)
4、每个队列都要绑定到交换机
5、生产者发送的消息，经过交换机，到达队列，实现，一个消息被多个消费者获取的目的

因为交换机没有存储消息的能力,在 rabbitmq 中只有队列存储消息的
能力.因为这时还没有队列,所以就会丢失;
小结:消息发送到了一个没有绑定队列的交换机时,消息就会丢失。

Direct Exchange:
处理路由键。
需要将一个队列绑定到交换机上，要求该消息与一个特定的路由键完全匹配。这是一个完整的匹配。如果一个队列
绑定到该交换机上要求路由键 “dog”，则只有被标记为“dog”的消息才被转发，不会转发 dog.puppy，也不会转发
dog.guard，只会转发 dog。

Topic Exchange:
将路由键和某模式进行匹配。
此时队列需要绑定要一个模式上。符号“#”匹配一个或多个词，符号“*”匹配一个词。因此“audit.#”能够匹配到
“audit.irs.corporate”，但是“audit.*” 只会匹配到“audit.irs”。


Fanout Exchange:
不处理路由键。你只需要将队列绑定到交换机上。发送消息到交换机都会被转发到与该交换机绑定的所有队列

Headers:
交换机基于包括headers和可选值的参数来做路由。Headers交换机和Topic交换机非常像，但是它是基于header的值而不是路由键来做路由的。一条消息被认为是匹配的，如果它header的值与绑定中指定的值相同的话。
一个叫做”x-match“的特殊的参数说明是否所有的header都必须匹配或者只需要有一个， 它有两种值， 默认为"all"，表示所有的header键值对都必须匹配；而"any"表示至少一个header键值对需要匹配。Headers可以被int或者string组成。

RabbitMQ 之消息确认机制(事务+Confirm)
在 Rabbitmq 中我们可以通过持久化来解决因为服务器异常而导致丢失的问题,
除此之外我们还会遇到一个问题:生产者将消息发送出去之后,消息到底有没有正确到达 Rabbit 服务器呢?如果不错
得数处理,我们是不知道的,(即 Rabbit 服务器不会反馈任何消息给生产者),也就是默认的情况下是不知道消息有没有正确到达;
导致的问题:消息到达服务器之前丢失,那么持久化也不能解决此问题,因为消息根本就没有到达 Rabbit 服务器!
RabbitMQ 为我们提供了两种方式:
1. 通过 AMQP 事务机制实现，这也是 AMQP 协议层面提供的解决方案；
2. 通过将 channel 设置成 confirm 模式来实现；

Confirm发送方确认模式
Confirm发送方确认模式使用和事务类似，也是通过设置Channel进行发送方确认的。

AMQP：
channel.txSelect()声明启动事务模式；

channel.txComment()提交事务；

channel.txRollback()回滚事务；

Confirm的三种实现方式：

方式一：channel.waitForConfirms()普通发送方确认模式；

方式二：channel.waitForConfirmsOrDie()批量确认模式；

方式三：channel.addConfirmListener()异步监听发送方确认模式；
Channel 对象提供的 ConfirmListener()回调方法只包含 deliveryTag（当前 Chanel 发出的消息序号），我们需要自己为
每一个 Channel 维护一个 unconfirm 的消息序号集合，每 publish 一条数据，集合中元素加 1，每回调一次 handleAck
方法，unconfirm 集合删掉相应的一条（multiple=false）或多条（multiple=true）记录。从程序运行效率上看，这个
unconfirm 集合最好采用有序集合 SortedSet 存储结构。实际上，SDK 中的 waitForConfirms()方法也是通过 SortedSet
维护消息序号的