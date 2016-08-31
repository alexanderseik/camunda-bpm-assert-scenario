package org.camunda.bpm.scenario.test.processes;

import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ReceiveTaskAction;
import org.camunda.bpm.scenario.delegate.EventSubscriptionDelegate;
import org.camunda.bpm.scenario.runner.ProcessStarter;
import org.camunda.bpm.scenario.runner.ScenarioRun;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class StartByMessageTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/processes/StartByMessageTest.bpmn"})
  public void testStartByKey() {

    when(scenario.waitsAtReceiveTask("ReceiveTask")).thenReturn(new ReceiveTaskAction() {
      @Override
      public void execute(EventSubscriptionDelegate message) throws Exception {
        message.receive();
      }
    });

    Scenario.run(scenario).startByKey("StartByMessageTest").execute();

    verify(scenario, times(1)).hasFinished("StartEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/processes/StartByMessageTest.bpmn"})
  public void testStartByMessage1() {

    when(scenario.waitsAtReceiveTask("ReceiveTask")).thenReturn(new ReceiveTaskAction() {
      @Override
      public void execute(EventSubscriptionDelegate message) throws Exception {
        message.receive();
      }
    });

    Scenario.run(scenario).startByMessage("msg_StartEvent1").execute();

    verify(scenario, times(1)).hasFinished("StartEvent1");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/processes/StartByMessageTest.bpmn"})
  public void testStartByStarterMessage1() {

    when(scenario.waitsAtReceiveTask("ReceiveTask")).thenReturn(new ReceiveTaskAction() {
      @Override
      public void execute(EventSubscriptionDelegate message) throws Exception {
        message.receive();
      }
    });

    Scenario.run(scenario).startBy(new ProcessStarter() {
      @Override
      public ProcessInstance start() {
        return rule.getRuntimeService().startProcessInstanceByMessage("msg_StartEvent1");
      }
    }).execute();

    verify(scenario, times(1)).hasFinished("StartEvent1");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/processes/StartByMessageTest.bpmn"})
  public void testStartByMessage2() {

    when(scenario.waitsAtReceiveTask("ReceiveTask")).thenReturn(new ReceiveTaskAction() {
      @Override
      public void execute(EventSubscriptionDelegate message) throws Exception {
        message.receive();
      }
    });

    Scenario.run(scenario).startByMessage("msg_StartEvent2").execute();

    verify(scenario, times(1)).hasFinished("StartEvent2");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/processes/StartByMessageTest.bpmn"})
  public void testStartByStarterMessage2() {

    when(scenario.waitsAtReceiveTask("ReceiveTask")).thenReturn(new ReceiveTaskAction() {
      @Override
      public void execute(EventSubscriptionDelegate message) throws Exception {
        message.receive();
      }
    });

    Scenario.run(scenario).startBy(new ProcessStarter() {
      @Override
      public ProcessInstance start() {
        return rule.getRuntimeService().startProcessInstanceByMessage("msg_StartEvent2");
      }
    }).execute();

    verify(scenario, times(1)).hasFinished("StartEvent2");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/processes/StartByMessageTest.bpmn"})
  public void testStartByMessageWithVariables() {

    when(scenario.waitsAtReceiveTask("ReceiveTask")).thenReturn(new ReceiveTaskAction() {
      @Override
      public void execute(EventSubscriptionDelegate message) throws Exception {
        message.receive();
      }
    });

    ScenarioRun run = Scenario.run(scenario).startBy(new ProcessStarter() {
      @Override
      public ProcessInstance start() {
        return rule.getRuntimeService().startProcessInstanceByMessage("msg_StartEvent1", withVariables("variable", true));
      }
    }).execute();

    HistoricVariableInstance variable = historyService().createHistoricVariableInstanceQuery().variableName("variable").singleResult();
    assertThat(variable).isNotNull();
    assertThat((Boolean) variable.getValue()).isTrue();
    verify(scenario, times(1)).hasFinished("StartEvent1");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

}
