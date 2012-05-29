/*
 * Copyright 2012 zhongl
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.zhongl

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import java.io.{ByteArrayInputStream, PrintStream, ByteArrayOutputStream}

/**
 * @author <a href="mailto:zhong.lunfu@gmail.com">zhongl<a>
 */
class CommandSuiteSpec extends FunSpec with ShouldMatchers {

  class ACommandSuite(line: String) extends CommandSuite("acs", "0.1.0", "A command suite") {
    val bout = new ByteArrayOutputStream()
    override protected val out = new PrintStream(bout)
    override protected val in  = new ByteArrayInputStream(line.getBytes)
  }

  private val backspace = "\u001B[K"

  private def moveCursor(i: Int) = "\u001B[" + i + "G"

  describe("Command suite") {

    it("should run as non-interactive") {
      val acs = new ACommandSuite("")
      acs main ("help quit".split("\\s+"))
      acs.bout.toString should be("\nUsage: quit\n\tterminate the process.\n\n")
    }

    it("should run as interactive") {
      val acs = new ACommandSuite("help\n")
      acs main (Array())
      acs.bout.toString should be(
        """acs> help
          |
          |help        display this infomation.
          |quit        terminate the process.
          |
          |acs> """.stripMargin.replaceAll("        ", "\t"))
    }

    it("should complete help command") {
      val acs = new ACommandSuite("h\t")
      acs main (Array())
      acs.bout.toString should be("acs> h" + moveCursor(6) + backspace + "help")
    }

    it("should complete help command argument") {
      val acs = new ACommandSuite("help q\t")
      acs main (Array())
      acs.bout.toString should be("acs> help q" + moveCursor(11) + backspace + "quit")
    }
  }

}
