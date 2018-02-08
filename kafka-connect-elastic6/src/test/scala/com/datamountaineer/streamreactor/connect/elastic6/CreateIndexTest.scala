/*
 * Copyright 2017 Datamountaineer.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datamountaineer.streamreactor.connect.elastic6

import com.datamountaineer.kcql.Kcql
import com.datamountaineer.streamreactor.connect.elastic6.indexname.CreateIndex
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.{Matchers, WordSpec}

class CreateIndexTest extends WordSpec with Matchers {
  "CreateIndex" should {
    "create an index name without suffix when suffix not set" in {
      val kcql = Kcql.parse("INSERT INTO index_name SELECT * FROM topicA")
      CreateIndex.getIndexName(kcql) shouldBe "index_name"
    }

    "create an index name with suffix when suffix is set" in {
      val kcql = Kcql.parse("INSERT INTO index_name SELECT * FROM topicA WITHINDEXSUFFIX=_suffix_{YYYY-MM-dd}")

      val formattedDateTime = new DateTime(DateTimeZone.UTC).toString("YYYY-MM-dd")
      CreateIndex.getIndexName(kcql) shouldBe s"index_name_suffix_$formattedDateTime"
    }
  }
}
