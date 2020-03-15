# sight-dotty ![Scala CI](https://github.com/ashwinbhaskar/sight-dotty/workflows/Scala%20CI/badge.svg)
Dotty client library for Sight APIs

# Usage

```
import sight.client.SightClient
import sight.types.APIKey
import sight.models.{Error, Pages}

val apiKey: Either[Error, APIKey] = APIKey("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")
val result: Either[Error, Pages] = apiKey.flatMap(k => SightClient(k).recognize(Seq("/user/john.doe/foo.pdf)))

```
