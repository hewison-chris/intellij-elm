package org.elm.ide.actions

import org.elm.TestProject
import org.elm.workspace.ElmWorkspaceTestBase


class ElmMoveNestedFileTest : ElmWorkspaceTestBase() {

    override fun setUp() {
        super.setUp()
        makeTestProjectFixture()
    }

    fun `test change module declaration`() {
        myFixture.moveFile("src/Name.elm", "src/B/")
        myFixture.checkResult("src/B/Name.elm",
            """
            module B.Name exposing (Name(..), name)
            
            type Name = First | Second
                
            name : Name -> String
            name n =
                case n of
                    First -> "Chris"
                    Second -> "Hewison"
        """.trimIndent(),
            true)
    }

    fun `test change import statements`() {
        myFixture.moveFile("src/Name.elm", "src/B")
        myFixture.checkResult("src/Main.elm",
            """
                module Main exposing (main)

                import String exposing(concat)
                
                import A.Hello
                import B.Name exposing (Name, name)
                import Html exposing (text)
                
                main : Html.Html msg
                main =  text (concat [(concat [B.C.D.A.Hello.hello, (name B.Name.First)]), (name B.Name.Second)])
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
                dir("A") {
                    elm(
                        "Hello.elm", """
                            module A.Hello exposing (hello)

                            hello : String
                            hello = "Hello"
                    """.trimIndent()
                    )
                }
                dir("B") {}
                elm("Main.elm", """
                    module Main exposing (main)

                    import String exposing(concat)
                    
                    import A.Hello
                    import Name exposing (Name, name)
                    import Html exposing (text)
                    
                    main : Html.Html msg
                    main =  text (concat [(concat [B.C.D.A.Hello.hello, (name Name.First)]), (name Name.Second)])
                """.trimIndent()
                )
                elm("Name.elm", """
                    module Name exposing (Name(..), name)

                    type Name = First | Second
                    
                    name : Name -> String
                    name n =
                        case n of
                            First -> "Chris"
                            Second -> "Hewison"
                """.trimIndent())
            }
        }
}
