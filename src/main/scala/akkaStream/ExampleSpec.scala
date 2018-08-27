package akkaStream

import collection.mutable.Stack
import org.scalatest._


/**
  * Author: fei2
  * Date:  18-8-27 下午6:02
  * Description:
  * Refer To:
  */
class ExampleSpec extends FlatSpec with Matchers {
  // TODO: 测试用例,待完善不能启动
  "A Stack" should "pop values in last-in-first-out order" in {
    val stack = new Stack[Int]
    stack.push(1)
    stack.push(2)
    stack.pop() should be (2)
    stack.pop() should be (1)
  }

  it should "throw NoSuchElementException if an empty stack is popped" in {
    val emptyStack = new Stack[Int]
    a [NoSuchElementException] should be thrownBy {
      emptyStack.pop()
    }
  }
}
