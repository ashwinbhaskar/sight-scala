# sight-scala ![Scala CI](https://github.com/ashwinbhaskar/sight-dotty/workflows/Scala%20CI/badge.svg)[![codecov](https://codecov.io/gh/ashwinbhaskar/sight-dotty/branch/master/graph/badge.svg)](https://codecov.io/gh/ashwinbhaskar/sight-dotty)
Dotty client library for Sight APIs. The Sight API is a text recognition service.


## Usage Scala 3.0.0-RC1

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
    import sight.models.Pages
    import sight.adt.Error

    val apiKey: Either[Error, APIKey] = APIKey("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")
    val files: Seq[String] = Seq("/user/john.doe/foo.pdf","/user/john.doe/baz/bmp")
    val result: Either[Error, Pages] = apiKey.flatMap(key => SightClient(key).recognize(files))

    import sight.extensions._

    val allTxt: Either[Error, Seq[String]] = result.map(_.allText)
    val allTxtGt: Either[Error, Seq[String]] = result.map(_.allTextWithConfidenceGreaterThan(0.2))
    ```
2. **Stream**: You can choose to get pages as and when they are processed. So this returns a LazySequence which can be consumed as a bunch of pages are processed. Use the function `recognizeStream` as shown below.

    ```
    import sight.types.APIKey
    import sight.models.Page
    import sight.adt.Error
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
