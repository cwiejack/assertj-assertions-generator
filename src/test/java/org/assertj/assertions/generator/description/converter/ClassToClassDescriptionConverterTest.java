/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2015 the original author or authors.
 */
package org.assertj.assertions.generator.description.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.assertj.assertions.generator.BeanWithExceptionsTest;
import org.assertj.assertions.generator.NestedClassesTest;
import org.assertj.assertions.generator.data.ArtWork;
import org.assertj.assertions.generator.data.Movie;
import org.assertj.assertions.generator.data.Team;
import org.assertj.assertions.generator.data.TreeEnum;
import org.assertj.assertions.generator.data.lotr.FellowshipOfTheRing;
import org.assertj.assertions.generator.data.nba.Player;
import org.assertj.assertions.generator.data.nba.PlayerAgent;
import org.assertj.assertions.generator.description.ClassDescription;
import org.assertj.assertions.generator.description.GetterDescription;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class ClassToClassDescriptionConverterTest implements NestedClassesTest, BeanWithExceptionsTest {
  private static ClassToClassDescriptionConverter converter;

  @BeforeClass
  public static void beforeAllTests() {
	converter = new ClassToClassDescriptionConverter();
  }

  @Test
  public void should_build_player_class_description() throws Exception {
	ClassDescription classDescription = converter.convertToClassDescription(Player.class);
	assertThat(classDescription.getClassName()).isEqualTo("Player");
	assertThat(classDescription.getClassNameWithOuterClass()).isEqualTo("Player");
	assertThat(classDescription.getPackageName()).isEqualTo("org.assertj.assertions.generator.data.nba");
    assertThat(classDescription.getGettersDescriptions()).hasSize(11);
  }

  @Test
  public void should_build_movie_class_description() throws Exception {
	ClassDescription classDescription = converter.convertToClassDescription(Movie.class);
	assertThat(classDescription.getClassName()).isEqualTo("Movie");
	assertThat(classDescription.getClassNameWithOuterClass()).isEqualTo("Movie");
	assertThat(classDescription.getPackageName()).isEqualTo("org.assertj.assertions.generator.data");
	assertThat(classDescription.getGettersDescriptions()).hasSize(3);
	assertThat(classDescription.getFieldsDescriptions()).hasSize(4);
	assertThat(classDescription.getDeclaredGettersDescriptions()).hasSize(2);
	assertThat(classDescription.getDeclaredFieldsDescriptions()).hasSize(3);
	assertThat(classDescription.getSuperType()).isEqualTo(ArtWork.class);
  }

  @Theory
  public void should_build_nestedclass_description(NestedClass nestedClass) throws Exception {
	Class<?> clazz = nestedClass.getNestedClass();
	ClassDescription classDescription = converter.convertToClassDescription(clazz);
	assertThat(classDescription.getClassName()).isEqualTo(clazz.getSimpleName());
	assertThat(classDescription.getClassNameWithOuterClass()).isEqualTo(nestedClass.getClassNameWithOuterClass());
	assertThat(classDescription.getPackageName()).isEqualTo(clazz.getPackage().getName());
	assertThat(classDescription.getGettersDescriptions()).hasSize(1);
  }

  @Theory
  public void should_build_getter_with_exception_description(GetterWithException getter) throws Exception {
	Class<?> clazz = getter.getBeanClass();
	ClassDescription classDescription = converter.convertToClassDescription(clazz);
	assertThat(classDescription.getClassName()).isEqualTo(clazz.getSimpleName());
	assertThat(classDescription.getClassNameWithOuterClass()).isEqualTo(clazz.getSimpleName());
	assertThat(classDescription.getPackageName()).isEqualTo(clazz.getPackage().getName());
	assertThat(classDescription.getGettersDescriptions()).hasSize(4);

	for (GetterDescription desc : classDescription.getGettersDescriptions()) {
	  if (desc.getPropertyName().equals(getter.getPropertyName())) {
		assertThat(desc.getExceptions()).containsOnly(getter.getExceptions());
		break;
	  }
	}
  }

  @Test
  public void should_build_class_description_for_iterable_of_primitive_type_array() throws Exception {
	class Type {
	  List<int[]> scores;

	  @SuppressWarnings("unused")
	  public List<int[]> getScores() {
		return scores;
	  }
	}
	ClassDescription classDescription = converter.convertToClassDescription(Type.class);
	assertThat(classDescription.getClassName()).isEqualTo("Type");
	assertThat(classDescription.getGettersDescriptions()).hasSize(1);
	GetterDescription getterDescription = classDescription.getGettersDescriptions().iterator().next();
	assertThat(getterDescription.isIterableType()).as("getterDescription must be iterable").isTrue();
	assertThat(getterDescription.getElementTypeName(Type.class.getPackage().getName())).isEqualTo("int[]");
	assertThat(getterDescription.isArrayType()).as("getterDescription must not be an array").isFalse();
  }

  @Test
  public void should_build_class_description_for_array_of_primitive_type_array() throws Exception {
	class Type {
	  int[][] scores;

	  @SuppressWarnings("unused")
	  public int[][] getScores() {
		return scores;
	  }
	}
	ClassDescription classDescription = converter.convertToClassDescription(Type.class);
	assertThat(classDescription.getClassName()).isEqualTo("Type");
	assertThat(classDescription.getGettersDescriptions()).hasSize(1);
	GetterDescription getterDescription = classDescription.getGettersDescriptions().iterator().next();
	assertThat(getterDescription.isIterableType()).as("getterDescription is an iterable ?").isFalse();
	assertThat(getterDescription.isArrayType()).as("getterDescription is an array ?").isTrue();
	assertThat(getterDescription.getElementTypeName(Type.class.getPackage().getName())).isEqualTo("int[]");
  }

  @Test
  public void should_build_class_description_for_enum_type() throws Exception {
	ClassDescription classDescription = converter.convertToClassDescription(TreeEnum.class);
	assertThat(classDescription.getClassName()).isEqualTo("TreeEnum");
	// should not contain getDeclaringClassGetter as we don't want to have hasDeclaringClass assertion
	assertThat(classDescription.getGettersDescriptions()).hasSize(1);
	GetterDescription getterDescription = classDescription.getGettersDescriptions().iterator().next();
	assertThat(getterDescription.isIterableType()).as("getterDescription must be iterable").isTrue();
	assertThat(getterDescription.getElementTypeName(TreeEnum.class.getPackage().getName())).isEqualTo("TreeEnum");
	assertThat(getterDescription.isArrayType()).as("getterDescription must be an array").isFalse();
  }

  @Test
  public void should_build_class_description_for_iterable_of_Object_type() throws Exception {
	// Given
	class Type {
	  List<Player[]> players;

	  @SuppressWarnings("unused")
	  public List<Player[]> getPlayers() {
		return players;
	  }
	}

	// When
	ClassDescription classDescription = converter.convertToClassDescription(Type.class);

	// Then
	assertThat(classDescription.getClassName()).isEqualTo("Type");
	assertThat(classDescription.getGettersDescriptions()).hasSize(1);
	GetterDescription getterDescription = classDescription.getGettersDescriptions().iterator().next();
	assertThat(getterDescription.isIterableType()).as("getterDescription must be iterable").isTrue();
	assertThat(getterDescription.getElementTypeName(Type.class.getPackage().getName())).isEqualTo("org.assertj.assertions.generator.data.nba.Player[]");
	assertThat(getterDescription.isArrayType()).as("getterDescription is not an array").isFalse();
  }

  @Test
  public void should_build_class_description_for_interface() throws Exception {
	// Given an interface
	// When
	ClassDescription classDescription = converter.convertToClassDescription(PlayerAgent.class);

	// Then
	assertThat(classDescription.getClassName()).isEqualTo("PlayerAgent");
	assertThat(classDescription.getSuperType()).isNull();
	assertThat(classDescription.getGettersDescriptions()).hasSize(1);
	GetterDescription getterDescription = classDescription.getGettersDescriptions().iterator().next();
	assertThat(getterDescription.isIterableType()).as("getterDescription is not iterable").isFalse();
	assertThat(getterDescription.getPropertyName()).isEqualTo("managedPlayer");
	assertThat(getterDescription.getTypeName()).isEqualTo("Player");
  }

  @Test
  public void should_build_fellowshipOfTheRing_class_description() throws Exception {
	ClassDescription classDescription = converter.convertToClassDescription(FellowshipOfTheRing.class);
	assertThat(classDescription.getClassName()).isEqualTo("FellowshipOfTheRing");
	assertThat(classDescription.getClassNameWithOuterClass()).isEqualTo("FellowshipOfTheRing");
	assertThat(classDescription.getClassNameWithOuterClassNotSeparatedByDots()).isEqualTo("FellowshipOfTheRing");
	assertThat(classDescription.getPackageName()).isEqualTo("org.assertj.assertions.generator.data.lotr");
	assertThat(classDescription.getGettersDescriptions()).hasSize(1);
  }

  @Test
  public void should_handle_toString() {
	ClassDescription classDescription = converter.convertToClassDescription(FellowshipOfTheRing.class);
	assertThat(classDescription.toString()).contains(FellowshipOfTheRing.class.getName());
  }

  @Test
  public void should_build_class_description_for_class_with_public_fields() throws Exception {
	ClassDescription classDescription = converter.convertToClassDescription(Team.class);
	assertThat(classDescription.getClassName()).isEqualTo("Team");
	assertThat(classDescription.getClassNameWithOuterClass()).isEqualTo("Team");
	assertThat(classDescription.getClassNameWithOuterClassNotSeparatedByDots()).isEqualTo("Team");
	assertThat(classDescription.getPackageName()).isEqualTo("org.assertj.assertions.generator.data");
	assertThat(classDescription.getGettersDescriptions()).extracting("propertyName").containsExactly("division");
	assertThat(classDescription.getFieldsDescriptions()).extracting("name").containsOnly("name",
	                                                                                     "oldNames",
	                                                                                     "westCoast",
	                                                                                     "rank",
	                                                                                     "players",
	                                                                                     "points",
	                                                                                     "victoryRatio");
  }

  @Test
  public void bug21_reflection_error_on_iterable_ParameterizedType() {
	class MySQLException extends SQLException {
	  private static final long serialVersionUID = 1L;

	  @SuppressWarnings("unused")
	  public SQLException getExceptionChain() {
		return null;
	  }
	}
	ClassDescription classDescription = converter.convertToClassDescription(MySQLException.class);
	// exceptionChain is a SQLException which is an Iterable<Throwable> but looking only at SQLException we can't deduce
	// iterable type
	assertThat(classDescription.getGettersDescriptions()).extracting("propertyName").contains("exceptionChain");
  }

  @Test
  public void should_only_describe_overriden_getter_once() {
	ClassDescription myClassDescription = converter.convertToClassDescription(ClassOverridingGetter.class);
	assertThat(myClassDescription.getGettersDescriptions()).extracting("propertyName").containsOnlyOnce("myList");
  }

  public interface InterfaceWithGetter {
	public abstract List<String> getMyList();
  }

  class ClassOverridingGetter implements InterfaceWithGetter {
	@Override
  public ArrayList<String> getMyList() {
	  return null;
	}
  }

}
