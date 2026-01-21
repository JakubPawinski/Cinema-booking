package cinema.booking.cinemabooking.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Architecture tests for the Cinema Booking application.
 */
@AnalyzeClasses(packages = "cinema.booking.cinemabooking", importOptions = {ImportOption.DoNotIncludeTests.class, ImportOption.DoNotIncludeJars.class})
public class ArchitectureTest {

    /**
     * LAYERED ARCHITECTURE RULES
     * Ensures that the application follows a strict layered architecture.
     */
    @ArchTest
    static final ArchRule layered_architecture_rule = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Controller").definedBy("..controller..")
            .layer("Service").definedBy("..service..")
            .layer("Repository").definedBy("..repository..")
            .layer("Dao").definedBy("..dao..")
            .layer("Config").definedBy("..config..")
            .layer("Model").definedBy("..model..")

            // Controllers are entry points and should not be injected into other layers
            .whereLayer("Controller").mayNotBeAccessedByAnyLayer()

            // Services should only be used by Controllers or Configuration classes
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Config")

            // Repositories should only be accessed by Services or DAOs
            .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service", "Dao")

            // DAOs should only be accessed by Services
            .whereLayer("Dao").mayOnlyBeAccessedByLayers("Service");

    /**
     * Ensures that classes in the 'controller' package are annotated correctly
     * and end with the suffix 'Controller'.
     */
    @ArchTest
    static final ArchRule controllers_should_be_named_correctly = classes()
            .that().resideInAPackage("..controller..")
            .and().areAnnotatedWith(org.springframework.stereotype.Controller.class)
            .or().areAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
            .should().haveSimpleNameEndingWith("Controller");

    /**
     * Ensures that classes in the 'service' package are annotated with @Service
     * and end with the suffix 'Service'.
     */
    @ArchTest
    static final ArchRule services_should_be_named_correctly = classes()
            .that().resideInAPackage("..service..")
            .and().areAnnotatedWith(org.springframework.stereotype.Service.class)
            .should().haveSimpleNameEndingWith("Service");

    /**
     * Ensures that interfaces in the 'repository' package end with the suffix 'Repository'.
     */
    @ArchTest
    static final ArchRule repositories_should_be_named_correctly = classes()
            .that().resideInAPackage("..repository..")
            .and().areInterfaces()
            .should().haveSimpleNameEndingWith("Repository");

    /**
     * Repositories should not depend on the Web layer (Controllers).
     */
    @ArchTest
    static final ArchRule repositories_should_not_depend_on_web_layer = noClasses()
            .that().resideInAPackage("..repository..")
            .should().dependOnClassesThat().resideInAPackage("..controller..");

    /**
     * Domain Entities (Models) should be independent.
     */
    @ArchTest
    static final ArchRule entities_should_be_independent = noClasses()
            .that().resideInAPackage("..model..")
            .should().dependOnClassesThat().resideInAPackage("..service..")
            .orShould().dependOnClassesThat().resideInAPackage("..controller..");

    /**
     * Prevents field injection (e.g. @Autowired private Service service).
     * Constructor injection is recommended for better testability and immutability.
     */
    @ArchTest
    static final ArchRule no_field_injection = GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

    /**
     * Prevents usage of System.out or System.err.
     * Proper Loggers (SLF4J, Log4j) should be used instead.
     */
    @ArchTest
    static final ArchRule no_access_to_standard_streams = GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;


    /**
     * Checks that there are no cyclic dependencies between packages.
     */
    @ArchTest
    static final ArchRule no_cycles_between_slices =
            com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices()
                    .matching("cinema.booking.cinemabooking.(*)..")
                    .should().beFreeOfCycles();
}