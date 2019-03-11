package com.demo;

import ch.qos.logback.core.net.SyslogOutputStream;
import org.activiti.engine.*;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Test
    public void contextLoads() {
        //testCreateProcessEngine();
        //testCreateProcessEngine2();
    }

   // @Test
    public void testCreateProcessEngine(){
        // 使用代码创建工作流需要的23张表
        ProcessEngineConfiguration  processEngineConfiguration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        //连接数据库的配置
        processEngineConfiguration.setJdbcDriver("com.mysql.jdbc.Driver");
        processEngineConfiguration.setJdbcUrl("jdbc:mysql://localhost:3306/activiti?useUnidcoe=true&characterEncoding=UTF-8&useSSL=true&serverTimezone=UTC");
        processEngineConfiguration.setJdbcUsername("root");
        processEngineConfiguration.setJdbcPassword("123");

        /**
         public static final String DB_SCHEMA_UPDATE_FALSE = "false";不能自动创建表，需要表存在
         public static final String DB_SCHEMA_UPDATE_CREATE_DROP = "create-drop";先删除表再创建表
         public static final String DB_SCHEMA_UPDATE_TRUE = "true";如果表不存在，自动创建表
         */

        processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        //工作流的核心对象，ProcessEnginee对象
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        System.out.println(processEngine);
    }
    @Test
    public void testCreateProcessEngine2(){
        /**使用配置文件创建工作流需要的23张表*/
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml")   //
                .buildProcessEngine();
        System.out.println(processEngine);
    }
    @Test
    public void testCreateProcessEngine3(){
        // 注：在Activiti中，在创建核心的流程引擎对象时会自动建表。如果程序正常执行，mysql会自动建库，然后创建23张表。
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.println(processEngine);
    }


    /**
     *
     * 发布流程
     */
    private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    @Test
    public void deployProcess(){
        //RepositoryService 管理流程定义 是Activiti的仓库服务类。所谓的仓库指流程定义文档的两个文件：bpmn文件和流程图片。
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.createDeployment().name("my").addClasspathResource("processes/abcd.bpmn")
                .deploy();
    }

    /*
    * 启动流程
    * RuntimeService
    * */
    @Test
    public void startProcess(){
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //根据Id ,key ,message 启动流程
        runtimeService.startProcessInstanceByKey("myProcess_1");
    }

    /*
    * 查看任务
    * TaskService
    * */
    @Test
    public void TaskProcess(){
        TaskService taskService = processEngine.getTaskService();
        //根据代理人查询任务
        List<Task> tasks = taskService.createTaskQuery().taskAssignee("emp").list();
        for(int i = 0; i < tasks.size(); i++){
            Task task = tasks.get(i);
        }
        for(Task task : tasks){
            System.out.println("id=="+task.getId()+
                                "Assignee=="+task.getAssignee()+
                                "name=="+task.getName()+
                                "time=="+task.getCreateTime());
        }
    }
    /*
     * 办理任务
     * handleService
     * */
    @Autowired
    private TaskService taskService;
    @Test
    public void handleProcess(){
       // TaskService taskService = processEngine.getTaskService();
        String taskId = "37502";
        taskService.complete(taskId);
    }

    /*
    * 查询定义流程
    *
    * id 是随机数
    * name 对应的name属性
    * key 是对应的Iid 属性
    *
    * */
    @Test
    public void queryProcessDefinition(){
        List<ProcessDefinition> processDefinitions = processEngine.getRepositoryService().createProcessDefinitionQuery().orderByDeploymentId().asc().list();
        processDefinitions.forEach(System.out :: println);
    }
}
