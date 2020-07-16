package org.pitest.mutationtest.arid;

import org.pitest.mutationtest.engine.MutationDetails;

public interface AridityDetectionManager {

  boolean decide(MutationDetails details);
}
