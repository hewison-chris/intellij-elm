package org.elm.ide.actions

import org.elm.TestProject
import org.elm.workspace.ElmWorkspaceTestBase


class ElmMoveNestedFileTest : ElmWorkspaceTestBase() {

    override fun setUp() {
        super.setUp()
        makeTestProjectFixture()
    }

    fun `test change module declaration`() {
        myFixture.moveFile("src/B/C/Hello.elm", "src/A")
        myFixture.checkResult("src/A/Hello.elm",
            """
                module A.Hello exposing (hello)
    
                hello : String
                hello = "Hello"
            """.trimIndent(),
            true)
    }

    fun `test change import statements`() {
        myFixture.moveFile("src/B/C/Hello.elm", "src/A")
        myFixture.checkResult("src/Main.elm",
            """
                module Main exposing (main)

                import A.Hello
                import Html exposing (text)
                import String exposing (append)
                
                main : Html.Html msg
                main = text A.Hello.hello
            """.trimIndent(),
            true)
    }

    private fun makeTestProjectFixture(): TestProject =
        buildProject {
            project(
                "elm.json", """
                {
                    "type": "application",
                    "source-directories": [
                        "src"
                    ],
                    "elm-version": "0.19.1",
                    "dependencies": {
                        "direct": {
                            "elm/browser": "1.0.2",
                            "elm/core": "1.0.0",
                            "elm/html": "1.0.0",
                            "elm/json": "1.0.0"
                        },
                        "indirect": {
                            "elm/time": "1.0.0",
                            "elm/url": "1.0.0",
                            "elm/virtual-dom": "1.0.3"
                        }
                    },
                    "test-dependencies": {
                        "direct": {},
                        "indirect": {}
                    }
                }
                """.trimIndent()
            )
            dir("src") {
                dir("A") {}
                dir("B") {
                    dir("C") {
                        elm(
                            "Hello.elm", """
                            module A.Hello exposing (hello)

                            hello : String
                            hello = "Hello"
                    """.trimIndent()
                        )
                    }
                }
                elm(
                    "Main.elm", """
                    module Main exposing (main)

                    import A.Hello
                    import Html exposing (text)
                    import String exposing (append)
                    
                    main : Html.Html msg
                    main = text A.Hello.hello
                """.trimIndent()
                )
            }
        }
}
