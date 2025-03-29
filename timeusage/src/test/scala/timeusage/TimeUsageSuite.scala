package timeusage

import org.apache.spark.sql.{ColumnName, DataFrame, Row}
import scala.util.Random
import scala.util.Properties.isWin
import org.apache.spark.sql.functions.{col}

class TimeUsageSuite extends munit.FunSuite:
  test("row should convert a list of strings to a Row with correct types") {
    val input = List("1.5", "2.0", "3.5", "abc", "4.0")
    val expected =
      Row(1.5, 2.0, 3.5, 0.0, 4.0) // Assuming invalid numbers default to 0.0

    val result = TimeUsage.row(input)

    assertEquals(result, expected)
  }

  test("classifiedColumns should correctly classify columns") {
    val columnNames = List(
      "t0101",
      "t0302",
      "t1105",
      "t1801abc",
      "t1803xyz", // Primary needs
      "t0501",
      "t1805xyz", // Working activities
      "t0701",
      "t0802",
      "t0903" // Other activities
    )

    val (primaryNeeds, workingActivities, otherActivities) =
      TimeUsage.classifiedColumns(columnNames)

    // Expected results
    val expectedPrimary =
      List("t0101", "t0302", "t1105", "t1801abc", "t1803xyz").map(col)
    val expectedWorking = List("t0501", "t1805xyz").map(col)
    val expectedOther = List("t0701", "t0802", "t0903").map(col)

    // Assertions
    assertEquals(
      primaryNeeds.map(_.toString()),
      expectedPrimary.map(_.toString()),
      "Primary needs classification failed"
    )
    assertEquals(
      workingActivities.map(_.toString()),
      expectedWorking.map(_.toString()),
      "Working activities classification failed"
    )
    assertEquals(
      otherActivities.map(_.toString()),
      expectedOther.map(_.toString()),
      "Other activities classification failed"
    )
  }
