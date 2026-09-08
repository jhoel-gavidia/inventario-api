package com.motorepuestos.inventario.support;

import com.motorepuestos.inventario.config.TestContainersConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
public abstract class AbstractIntegrationTest {
}
