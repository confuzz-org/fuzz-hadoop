# Bug

 Commit with fix:  https://github.com/Shubhi-Jain98/hadoop/commit/91930995123a18ee4b87f4087c0f1510effcc2d2

### Findings

|  | Failures | Errors |
| ------------- | ------------- | ------------- |
| Without Fix   | 6     | 12    |
| With Fix     | 3       | 15      |

**GOOD FAIL -> GOOD PASS** <br/>
On fixing the bug, the following three value sets, FailToPassValueSets, which were failing are now green. </br>
FailToPassValueSets = { </br>
&emsp;&emsp;&emsp;&emsp;  {ONEHOUR, 0.25, 1, TWOHOURPERIOD, ResourceOverCommitException.class}, </br>
&emsp;&emsp;&emsp;&emsp;  {ONEHOUR, 0.25, 4, TWOHOURPERIOD, ResourceOverCommitException.class},</br>
&emsp;&emsp;&emsp;&emsp;  {ONEHOUR, 1, 1, TWOHOURPERIOD, ResourceOverCommitException.class}}

**BAD PASS -> BAD FAIL** <br/>
Found the below value sets, PassToErrorValueSets, which throws error after the handling for subtractTestNonNegative operator is done. </br>
PassToErrorValueSets = { </br>
&emsp;&emsp;&emsp;&emsp;  {ONEHOUR, 0.25, 1, TWOHOURPERIOD, null}, </br>
&emsp;&emsp;&emsp;&emsp;  {ONEHOUR, 0.25, 4, TWOHOURPERIOD, null},</br>
&emsp;&emsp;&emsp;&emsp;  {ONEHOUR, 1, 1, TWOHOURPERIOD, null}}

## Bug Report

**Title:** </br>
When dealing with null values of map, the handling of operator subtractTestNonNegative is missing.
</br>

**Function Name:** </br> 
combineValue(RLEOperator op, ResourceCalculator resCalc, Resource clusterResource, Entry<Long, Resource> eA, Entry<Long, Resource> eB)
</br>

**Severity/Priority:** Medium
</br>

**Description / Reason:** </br>
The call to **[merge](https://github.com/apache/hadoop/blob/71778a6cc5780a8339e0e7c08a94009af3e70697/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-resourcemanager/src/main/java/org/apache/hadoop/yarn/server/resourcemanager/reservation/NoOverCommitPolicy.java#L52)** function in **validate(Plan plan, ReservationAllocation reservation)** sends the operator as subtractTestNonNegative.
````
RLESparseResourceAllocation
  .merge(plan.getResourceCalculator(), plan.getTotalCapacity(),
    available, ask,
    RLESparseResourceAllocation.RLEOperator.subtractTestNonNegative,
    reservation.getStartTime(), reservation.getEndTime()); 
````
But since, there is [no handling present for subtractTestNonNegative operator](https://github.com/apache/hadoop/blob/71778a6cc5780a8339e0e7c08a94009af3e70697/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-resourcemanager/src/main/java/org/apache/hadoop/yarn/server/resourcemanager/reservation/RLESparseResourceAllocation.java#L446) when dealing with null values, the merge function returns successfully and [fails in the below code block](https://github.com/apache/hadoop/blob/71778a6cc5780a8339e0e7c08a94009af3e70697/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-resourcemanager/src/test/java/org/apache/hadoop/yarn/server/resourcemanager/reservation/BaseSharingPolicyTest.java#L146)
````
    if (expectedError != null) {
        System.out.println(plan.toString());
        fail();
    }
````
Below is the Call hierarchy where combineValue function returns successfully
</br>[combineValue:446, RLESparseResourceAllocation](https://github.com/apache/hadoop/blob/71778a6cc5780a8339e0e7c08a94009af3e70697/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-resourcemanager/src/main/java/org/apache/hadoop/yarn/server/resourcemanager/reservation/RLESparseResourceAllocation.java#L446)
</br>[merge:377, RLESparseResourceAllocation](https://github.com/apache/hadoop/blob/71778a6cc5780a8339e0e7c08a94009af3e70697/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-resourcemanager/src/main/java/org/apache/hadoop/yarn/server/resourcemanager/reservation/RLESparseResourceAllocation.java#L377)
</br>[merge:312, RLESparseResourceAllocation](https://github.com/apache/hadoop/blob/71778a6cc5780a8339e0e7c08a94009af3e70697/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-resourcemanager/src/main/java/org/apache/hadoop/yarn/server/resourcemanager/reservation/RLESparseResourceAllocation.java#L312)
</br>[validate:52, NoOverCommitPolicy](https://github.com/apache/hadoop/blob/71778a6cc5780a8339e0e7c08a94009af3e70697/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-resourcemanager/src/main/java/org/apache/hadoop/yarn/server/resourcemanager/reservation/NoOverCommitPolicy.java#L52)
</br>[addReservation:348, InMemoryPlan](https://github.com/apache/hadoop/blob/71778a6cc5780a8339e0e7c08a94009af3e70697/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-resourcemanager/src/main/java/org/apache/hadoop/yarn/server/resourcemanager/reservation/InMemoryPlan.java#L348)
</br>[runTest:141, BaseSharingPolicyTest](https://github.com/apache/hadoop/blob/71778a6cc5780a8339e0e7c08a94009af3e70697/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-resourcemanager/src/test/java/org/apache/hadoop/yarn/server/resourcemanager/reservation/BaseSharingPolicyTest.java#L141)
</br>[testAllocation:87, TestNoOverCommitPolicy](https://github.com/apache/hadoop/blob/71778a6cc5780a8339e0e7c08a94009af3e70697/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-resourcemanager/src/test/java/org/apache/hadoop/yarn/server/resourcemanager/reservation/TestNoOverCommitPolicy.java#L83)
</br>

**Detailed Description:** </br>
The handling for the subtractTestNonNegative operator is present at two places in the class RLESparseResourceAllocation.
1. First is when the [special handling for null values](https://github.com/apache/hadoop/blob/71778a6cc5780a8339e0e7c08a94009af3e70697/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-resourcemanager/src/main/java/org/apache/hadoop/yarn/server/resourcemanager/reservation/RLESparseResourceAllocation.java#L322) is done for operands, Map_A and Map_B. 
2. Second, when iterating over all {key, value} pairs of Map_A and Map_B, [no {key, value} pair is null](https://github.com/apache/hadoop/blob/71778a6cc5780a8339e0e7c08a94009af3e70697/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-resourcemanager/src/main/java/org/apache/hadoop/yarn/server/resourcemanager/reservation/RLESparseResourceAllocation.java#L460). 
</br></br> But, when iterating over all {key, value} pairs of Map_A and Map_B, if any key in Map_A is null or its value is null, the handling for subtractTestNonNegative operator is missing.
For instance, let's take below two operands, Map_A: a and Map_B: b </br>
````
a = {TreeMap@3453}  size = 2
   {Long@3462} 0 -> {LightWeightResource@3463} "<memory:1024000, vCores:1000>"
   {Long@3472} 7200000 -> null
b = {TreeMap@3454}  size = 4
   {Long@3462} 0 -> {LightWeightResource@3464} "<memory:256000, vCores:250>"
   {Long@3479} 2232922 -> {Resources$FixedValueResource@3480} "<memory:0, vCores:0>"
   {Long@3481} 5832922 -> {LightWeightResource@3482} "<memory:256000, vCores:250>"
   {Long@3483} 7200000 -> {LightWeightResource@3464} "<memory:256000, vCores:250>"
````
Here, the 1st check for subtractTestNonNegative will pass since `a!=null` and `a is not empty`. 
Next, we will iterate over the {key, value} pairs in a and b. Let current iterators be curA and curB. </br>
When `curA = {TreeMap$Entry@3409} 7200000 -> null` and `curB = {TreeMap$Entry@3410} 7200000 -> "<memory:256000, vCores:250>"` </br>
The current code will return the value of curB, which is incorrect since the operator is subtractTestNonNegative. </br>
The correct value should be negate(curB) if it is non-negative and should throw an exception if negate(curB) is negative.

**Steps to reproduce:**
1. cd to hadoop repository, "cd hadoop"
2. Go to, "cd hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-resourcemanager"
3. Run, "mvn test -Dtest=TestNoOverCommitPolicy"
</br>
<ins>NOTE:</ins> Please make sure that the Array in TestNoOverCommitPolicy.java class contains value sets in FailToPassValueSets and PassToErrorValueSets.
</br>

**Expected Results:** </br>
The code should pass for value sets in FailToPassValueSets and throw an error for value sets in PassToErrorValueSets.
</br>

**Actual Results:**</br>
The code is failing for value sets in FailToPassValueSets and passing for value sets in PassToErrorValueSets.
</br>

**Proposed Solution/s:** </br>
Added the handling for subtractTestNonNegative when dealing with null values. </br>
````
if (op == RLEOperator.subtract || op == RLEOperator.subtractTestNonNegative) {
    if (op == RLEOperator.subtractTestNonNegative && (Resources.fitsIn(Resources.negate(eB.getValue()), ZERO_RESOURCE)
            && !Resources.equals(Resources.negate(eB.getValue()), ZERO_RESOURCE))) {
        throw new PlanningException(
            "RLESparseResourceAllocation: merge failed as the "
                + "resulting RLESparseResourceAllocation would be negative");
    }
    return Resources.negate(eB.getValue());
}
````

### Questions to be investigated...
1. The tests are flaky; the test passes for some value set and randomly fails for the same value set. Why is this so?
2. The count of number_of_failures and number_of_errors remains the same. For example, in the above table, three value sets made a transition from (good, fail) to (good, pass), but on the other hand, three value sets that were passing earlier (bad, pass) are now throwing error (bad, fail).
   <br/> The value sets which moved from (good, fail) to (good pass) and the value sets introduced as errors (bad, fail) are different.
   <br/> Why is the sum of failures and errors the same?
