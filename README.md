# json2tsv

Convert json log to tab-separated values.

## How to build

```
$ cd json2tsv
$ ./sbt assembly
```

## Example
```
$ echo '{"foo": "abc", "bar": { "piyo": 3 }}' | java -jar target/json2tsv-assembly-0.1-SNAPSHOT.jar -p foo -p bar.piyo
abc     3
```