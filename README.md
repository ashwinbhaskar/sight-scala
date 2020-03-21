# sight-dotty ![Scala CI](https://github.com/ashwinbhaskar/sight-dotty/workflows/Scala%20CI/badge.svg)[![codecov](https://codecov.io/gh/ashwinbhaskar/sight-dotty/branch/master/graph/badge.svg)](https://codecov.io/gh/ashwinbhaskar/sight-dotty)
Dotty client library for Sight APIs. For details please have a look at the official [clojure library](https://github.com/siftrics/sight-clojure)


# Usage

Works with Dotty `0.22`

sbt 

```
libraryDependencies += "io.github.ashwinbhaskar" % "sight-client_0.22" % "0.1.0"
```

In the code

```
import sight.client.SightClient
import sight.types.APIKey
import sight.models.{Error, Pages}

val apiKey: Either[Error, APIKey] = APIKey("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")
val files: Seq[String] = Seq("/user/john.doe/foo.pdf","/user/john.doe/baz/bmp")
val result: Either[Error, Pages] = apiKey.flatMap(key => SightClient(key).recognize(files))

import sight.extensions.pageOps

val allTxt: Either[Error, Seq[String]] = result.map(_.allText)
val allTxtGt: Either[Error, Seq[String]] = result.map(_.allTextWithConfidenceGreaterThan(0.2))
```

# Usage in Scala 2.13

There is an an going work on tasty reader for Scala 2.13 (https://github.com/scalacenter/scala/tree/tasty_reader) which can recompile the tasty to Scala 2.13. Until that work is completed, I believe this library cannot be used in scala 2.13.
