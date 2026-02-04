package com.github.langsdorf.keycloak.brazilian_tin;

import org.keycloak.validate.Validator;
import org.keycloak.validate.ValidatorFactory;

import java.util.ArrayList;
import java.util.List;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ConfiguredProvider;
import org.keycloak.provider.ProviderConfigProperty;

public class BrazilianTINValidatorFactory implements ValidatorFactory, ConfiguredProvider {

    public static final String ID = "brazilian-tin-validator";

    @Override
    public Validator create(KeycloakSession session) {
        return new BrazilianTINValidator();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        System.out.println("Brazilian TIN Validator Factory initialized");
    }

    @Override
    public String getHelpText() {
        return "Validates Brazilian Tax Identification Numbers (CPF and CNPJ).";
    }

    private static final List<ProviderConfigProperty> configProperties = new ArrayList<ProviderConfigProperty>();

    static {
        ProviderConfigProperty bypassStringProperty = new ProviderConfigProperty();
        bypassStringProperty.setType(ProviderConfigProperty.STRING_TYPE);
        bypassStringProperty.setName("bypassString");
        bypassStringProperty.setLabel("Bypass String");
        bypassStringProperty.setHelpText("A string that, if entered, will bypass validation.");
        bypassStringProperty.setDefaultValue("FOREIGN");

        ProviderConfigProperty enableBypassProperty = new ProviderConfigProperty();
        enableBypassProperty.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        enableBypassProperty.setName("enableBypass");
        enableBypassProperty.setLabel("Enable Bypass");
        enableBypassProperty.setHelpText("Enable or disable the bypass functionality.");
        enableBypassProperty.setDefaultValue("false");

        configProperties.add(bypassStringProperty);
        configProperties.add(enableBypassProperty);
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

}
