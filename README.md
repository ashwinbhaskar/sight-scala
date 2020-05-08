# sight-dotty ![Scala CI](https://github.com/ashwinbhaskar/sight-dotty/workflows/Scala%20CI/badge.svg)[![codecov](https://codecov.io/gh/ashwinbhaskar/sight-dotty/branch/master/graph/badge.svg)](https://codecov.io/gh/ashwinbhaskar/sight-dotty)
Dotty client library for Sight APIs. The Sight API is a text recognition service.


## Usage Dotty 0.22, 0.23

### Dependency 

```
libraryDependencies += "io.github.ashwinbhaskar" %% "sight-client" % "0.1.1"
```

### API Key

Grap an APIKey from the [Sight Dashboard](https://siftrics.com/)

### Code

1. **One Shot**: If your files contain a lot of pages then this will take some time as this will return only after all the pages have been processed. Use the function `recognize` as shown below.
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
2. **Stream**: You can choose to get pages as and when they are processed. So this returns a LazySequence which can be consumed as a bunch of pages are processed. Use the function `recognizeStream` as shown below.

    ```
    import sight.types.APIKey
    import sight.models.{Error, Page}
    import sight.client.SightClient

    val apiKey: Either[Error, APIKey] = APIKey("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")
    val files = Seq("/user/john.doe/foo.pdf","/user/john.doe/baz/bmp")Downloads/flight-euro.pdf")
    apiKey match
        case Right(k) => 
            val result: LazyList[Either[Error, Seq[Page]]] = SightClient(k).recognizeStream(files)
            result.foreach(println)
        case Left(error) => println(e)
    ```

## Official API Documentation 

Here is the official [API Documentation](https://siftrics.com/docs/sight.html)

## Usage in Scala 2.13

There is an an going work on tasty reader for Scala 2.13 (https://github.com/scalacenter/scala/tree/tasty_reader) which can recompile the tasty to Scala 2.13. Until that work is completed, I believe this library cannot be used in scala 2.13.
