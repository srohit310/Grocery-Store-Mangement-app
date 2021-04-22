package com.bumptech.glide.annotation.compiler;

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideModule;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Generates an empty class with an annotation containing the class names of one or more
 * LibraryGlideModules and/or one or more GlideExtensions.
 *
 * <p>We use a separate class so that LibraryGlideModules and GlideExtensions written in libraries
 * can be bundled into an AAR and later retrieved by the annotation processor when it processes the
 * AppGlideModule in an application.
 *
 * <p>The output file generated by this class with a LibraryGlideModule looks like this:
 *
 * <pre>
 * <code>
 *  {@literal @com.bumptech.glide.annotation.compiler.Index(}
 *      modules = "com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule"
 *  )
 *  public class Indexer_GlideModule_com_bumptech_glide_integration_okhttp3_OkHttpLibraryGlideModule
 *  {
 *  }
 * </code>
 * </pre>
 *
 * <p>The output file generated by this class with a GlideExtension looks like this:
 *
 * <pre>
 * <code>
 *  {@literal @com.bumptech.glide.annotation.compiler.Index(}
 *      extensions = "com.bumptech.glide.integration.gif.GifOptions"
 *  )
 *  public class Indexer_GlideExtension_com_bumptech_glide_integration_gif_GifOptions {
 *  }
 * </code>
 * </pre>
 */
final class IndexerGenerator {
  private static final String INDEXER_NAME_PREFIX = "GlideIndexer_";
  private static final int MAXIMUM_FILE_NAME_LENGTH = 255;
  private final ProcessorUtil processorUtil;

  IndexerGenerator(ProcessorUtil processorUtil) {
    this.processorUtil = processorUtil;
  }

  TypeSpec generate(List<TypeElement> types) {
    List<TypeElement> modules = new ArrayList<>();
    List<TypeElement> extensions = new ArrayList<>();
    for (TypeElement element : types) {
      if (processorUtil.isExtension(element)) {
        extensions.add(element);
      } else if (processorUtil.isLibraryGlideModule(element)) {
        modules.add(element);
      } else {
        throw new IllegalArgumentException("Unrecognized type: " + element);
      }
    }
    if (!modules.isEmpty() && !extensions.isEmpty()) {
      throw new IllegalArgumentException(
          "Given both modules and extensions, expected one or the "
              + "other. Modules: "
              + modules
              + " Extensions: "
              + extensions);
    }
    if (!modules.isEmpty()) {
      return generate(types, GlideModule.class);
    } else {
      return generate(types, GlideExtension.class);
    }
  }

  private TypeSpec generate(
      List<TypeElement> libraryModules, Class<? extends Annotation> annotation) {
    AnnotationSpec.Builder annotationBuilder = AnnotationSpec.builder(Index.class);

    String value = getAnnotationValue(annotation);
    for (TypeElement childModule : libraryModules) {
      annotationBuilder.addMember(value, "$S", ClassName.get(childModule).toString());
    }

    StringBuilder indexerNameBuilder =
        new StringBuilder(INDEXER_NAME_PREFIX + annotation.getSimpleName() + "_");
    for (TypeElement element : libraryModules) {
      indexerNameBuilder.append(element.getQualifiedName().toString().replace(".", "_"));
      indexerNameBuilder.append("_");
    }
    indexerNameBuilder =
        new StringBuilder(indexerNameBuilder.substring(0, indexerNameBuilder.length() - 1));
    String indexerName = indexerNameBuilder.toString();
    // If the indexer name has too many packages/modules, it can exceed the file name length
    // allowed by the file system, which can break compilation. To avoid that, fall back to a
    // deterministic UUID.
    if (indexerName.length() >= (MAXIMUM_FILE_NAME_LENGTH - INDEXER_NAME_PREFIX.length())) {
      indexerName =
          INDEXER_NAME_PREFIX
              + UUID.nameUUIDFromBytes(indexerName.getBytes()).toString().replace("-", "_");
    }

    return TypeSpec.classBuilder(indexerName)
        .addAnnotation(annotationBuilder.build())
        .addModifiers(Modifier.PUBLIC)
        .build();
  }

  private static String getAnnotationValue(Class<? extends Annotation> annotation) {
    if (annotation == GlideModule.class) {
      return "modules";
    } else if (annotation == GlideExtension.class) {
      return "extensions";
    } else {
      throw new IllegalArgumentException("Unrecognized annotation: " + annotation);
    }
  }
}
