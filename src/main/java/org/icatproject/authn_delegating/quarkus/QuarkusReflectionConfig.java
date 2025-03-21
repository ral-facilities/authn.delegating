package org.icatproject.authn_delegating.quarkus;

import io.quarkus.runtime.annotations.RegisterForReflection;

import org.icatproject.authn_delegating.DelegatingAuthenticator;

@RegisterForReflection(targets={
	DelegatingAuthenticator.RequestJsonParam.class
})
public class QuarkusReflectionConfig {
}
