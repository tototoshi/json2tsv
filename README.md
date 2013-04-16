# json2tsv

Convert json log to tab-separated values.

## How to install

```
$ cd json2tsv
$ ./install
```

`json2tsv` will be installed in $HOME/bin.


## Example
```
$ echo '{"foo": "abc", "bar": { "piyo": 3 }}' | json2tsv -p foo -p bar.piyo
abc     3
```