# 0

## command
```shell
mvn test "-Dtest=TestSample#testScript[0:*]" -Dmaven.antrun.skip -Dos.detected.classifier=osx-x86_64
````
## surefire output
```
August 06, 2022 10:58:30 afternoon org.apache.hadoop.http.TestSample testScript
INFO: expected: true
August 06, 2022 10:58:30 afternoon org.apache.hadoop.http.TestSample testScript
INFO: input: abc
August 06, 2022 10:58:30 afternoon org.apache.hadoop.http.TestSample testScript
INFO: n: 20
```

# 1
## command
```shell
mvn test "-Dtest=TestSample#testScript[1:*]" -Dmaven.antrun.skip -Dos.detected.classifier=osx-x86_64
````
## surefire output
```
August 06, 2022 11:00:51 afternoon org.apache.hadoop.http.TestSample testScript
INFO: expected: true
August 06, 2022 11:00:51 afternoon org.apache.hadoop.http.TestSample testScript
INFO: input: abc
August 06, 2022 11:00:51 afternoon org.apache.hadoop.http.TestSample testScript
INFO: n: 10
```

# 5
## command
```shell
mvn test "-Dtest=TestSample#testScript[5:*]" -Dmaven.antrun.skip -Dos.detected.classifier=osx-x86_64
````
## surefire output
```
August 06, 2022 11:02:23 afternoon org.apache.hadoop.http.TestSample testScript
INFO: expected: false
August 06, 2022 11:02:23 afternoon org.apache.hadoop.http.TestSample testScript
INFO: input: abc
August 06, 2022 11:02:23 afternoon org.apache.hadoop.http.TestSample testScript
INFO: n: 20
```
