package org.camunda.bpm.scenario.test.waitstates;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.EventBasedGatewayAction;
import org.camunda.bpm.scenario.delegate.EventBasedGatewayDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class EventBasedGatewayWithoutTimerTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/EventBasedGatewayWithoutTimerTest.bpmn"})
  public void testReceiveMessage() {

    when(scenario.actsOnEventBasedGateway("EventBasedGateway")).thenReturn(new EventBasedGatewayAction() {
      @Override
      public void execute(EventBasedGatewayDelegate gateway) {
        gateway.getEventSubscription("MessageIntermediateCatchEvent").receive();
      }
    });

    Scenario.run(scenario).startBy("EventBasedGatewayWithoutTimerTest").execute();

    verify(scenario, times(1)).hasFinished("EventBasedGateway");
    verify(scenario, times(1)).hasFinished("MessageIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/EventBasedGatewayWithoutTimerTest.bpmn"})
  public void testDoNothing() {

    when(scenario.actsOnEventBasedGateway("EventBasedGateway")).thenReturn(new EventBasedGatewayAction() {
      @Override
      public void execute(EventBasedGatewayDelegate gateway) {
        // Do nothing means process remains here because of no timers
      }
    });

    Scenario.run(scenario).startBy("EventBasedGatewayWithoutTimerTest").execute();

    verify(scenario, times(1)).hasStarted("EventBasedGateway");
    verify(scenario, never()).hasFinished("EventBasedGateway");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test(expected=AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/EventBasedGatewayWithoutTimerTest.bpmn"})
  public void testDoNotDealWithEventBasedGateway() {

    Scenario.run(scenario).startBy("EventBasedGatewayWithoutTimerTest").execute();

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/EventBasedGatewayWithoutTimerTest.bpmn"})
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.actsOnEventBasedGateway("EventBasedGateway")).thenReturn(new EventBasedGatewayAction() {
      @Override
      public void execute(EventBasedGatewayDelegate gateway) {
        gateway.getEventSubscription("MessageIntermediateCatchEvent").receive();
      }
    });

    when(otherScenario.actsOnEventBasedGateway("EventBasedGateway")).thenReturn(new EventBasedGatewayAction() {
      @Override
      public void execute(EventBasedGatewayDelegate gateway) {
      }
    });

    Scenario.run(otherScenario).startBy("EventBasedGatewayWithoutTimerTest").execute();
    Scenario.run(scenario).startBy("EventBasedGatewayWithoutTimerTest").execute();

    verify(scenario, times(1)).hasFinished("EventBasedGateway");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasFinished("EventBasedGateway");

  }

}
