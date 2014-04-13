swagger-vraptor-maven-plugin
============================

License
=======

Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements. See the NOTICE file
distributed with this work for additional information
regarding copyright ownership. The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License. You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the
specific language governing permissions and limitations
under the License.

Usage
=====

			<plugin>
				<groupId>com.github.lucasaraujo</groupId>
				<artifactId>swagger-vraptor-maven-plugin</artifactId>
				<version>0.1</version>
				<configuration>
					<apiVersion>1.0.0</apiVersion>
					<basePath>/</basePath>
					<locations>
						<param>org.company.controller</param>
					</locations>
					<swaggerDirectory>${basedir}/src/main/webapp/api-docs</swaggerDirectory>
				</configuration>
				<executions>
					<execution>
						<phase>process-classes</phase>
						<goals>
							<goal>generate</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
