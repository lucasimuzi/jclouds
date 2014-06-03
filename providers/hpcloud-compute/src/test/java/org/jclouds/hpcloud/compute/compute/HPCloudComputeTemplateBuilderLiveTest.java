/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.hpcloud.compute.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.OsFamilyVersion64Bit;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseTemplateBuilderLiveTest;
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;

@Test(groups = "live", singleThreaded = true, testName = "HPCloudComputeTemplateBuilderLiveTest")
public class HPCloudComputeTemplateBuilderLiveTest extends BaseTemplateBuilderLiveTest {

   public HPCloudComputeTemplateBuilderLiveTest() {
      provider = "hpcloud-compute";
   }

   @Override
   protected Predicate<OsFamilyVersion64Bit> defineUnsupportedOperatingSystems() {
      return Predicates.not(new Predicate<OsFamilyVersion64Bit>() {

         @Override
         public boolean apply(OsFamilyVersion64Bit input) {
            switch (input.family) {
               case UBUNTU:
                  return (input.version.equals("") || input.version.matches("(10.04)|(11.10)|(12.04)|(12.10)|(13.04)|(13.10)")) && input.is64Bit;
               case DEBIAN:
                  return input.is64Bit && !input.version.matches("(5.[0-9])|(6.[0-9])|(7.[0-9])");
               case CENTOS:
                  return (input.version.equals("") || input.version.matches("(5.0)|(5.8)|(6.3)|(6.5)")) && input.is64Bit;
               case WINDOWS:
                  return input.version.equals("") || input.version.equals("2008") || (input.version.equals("2008 R2") && input.is64Bit);
               default:
                  return false;
            }
         }

      });
   }

   @Test
   public void testTemplateBuilder() {
      Template defaultTemplate = this.view.getComputeService().templateBuilder().build();
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "12.04");
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertTrue(defaultTemplate.getImage().getName().startsWith("Ubuntu Server 12.04"));
      assertEquals(defaultTemplate.getImage().getDefaultCredentials().getUser(), "ubuntu");
      assertEquals(defaultTemplate.getLocation().getId(), "region-a.geo-1");
      assertEquals(defaultTemplate.getImage().getLocation().getId(), "region-a.geo-1");
      assertEquals(defaultTemplate.getHardware().getLocation().getId(), "region-a.geo-1");
      assertEquals(defaultTemplate.getOptions().as(NovaTemplateOptions.class).shouldAutoAssignFloatingIp(), true);
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
   }

   @Override
   protected Set<String> getIso3166Codes() {
      return ImmutableSet.<String> of("US-NV", "US-VA");
   }
}
