/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.atlas.repository.store.graph;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import org.apache.atlas.ApplicationProperties;
import org.apache.atlas.AtlasErrorCode;
import org.apache.atlas.AtlasException;
import org.apache.atlas.RepositoryMetadataModule;
import org.apache.atlas.exception.AtlasBaseException;
import org.apache.atlas.model.typedef.AtlasEntityDef;
import org.apache.atlas.repository.graph.AtlasGraphProvider;
import org.apache.atlas.repository.store.graph.v1.AtlasAbstractDefStoreV1;
import org.apache.atlas.type.AtlasTypeUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Tests for AtlasEntityStoreV1
 */
@Guice(modules = RepositoryMetadataModule.class)
public class AtlasEntityDefStoreV1Test {

    @Inject
    private
    AtlasEntityDefStore entityDefStore;

    @DataProvider
    public Object[][] invalidAttributeNameWithReservedKeywords(){
        AtlasEntityDef invalidAttrNameType =
            AtlasTypeUtil.createClassTypeDef("Invalid_Attribute_Type", "description", ImmutableSet.<String>of(),
                AtlasTypeUtil.createRequiredAttrDef("order", "string"),
                AtlasTypeUtil.createRequiredAttrDef("limit", "string"));

        return new Object[][] {{
            invalidAttrNameType
        }};
    }

    @Test(dataProvider = "invalidAttributeNameWithReservedKeywords")
    public void testCreateTypeWithReservedKeywords(AtlasEntityDef atlasEntityDef) throws AtlasException {
        try {
            ApplicationProperties.get().setProperty(AtlasAbstractDefStoreV1.ALLOW_RESERVED_KEYWORDS, false);
            entityDefStore.create(atlasEntityDef, null);
        } catch (AtlasBaseException e) {
            Assert.assertEquals(e.getAtlasErrorCode(), AtlasErrorCode.ATTRIBUTE_NAME_INVALID);
        }
    }

    @AfterClass
    public void clear(){
        AtlasGraphProvider.cleanup();
    }
}
