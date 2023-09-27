import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.PostgrestBuilder
import io.github.jan.supabase.postgrest.query.PostgrestResult
import io.ktor.http.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MainKtTest {

    private lateinit var supabaseClient: SupabaseClient

    @BeforeTest
    fun setUp() {

        val supabaseClient = mockk<SupabaseClient>()

        val postgrest = mockk<Postgrest>()
        val postgrestBuilder = mockk<PostgrestBuilder>()
        val postgrestResult = PostgrestResult(body = null, headers = Headers.Empty)

        mockkStatic(SupabaseClient::postgrest)
        every { supabaseClient.postgrest } returns postgrest
        every { postgrest["path"] } returns postgrestBuilder
        // seems inline functions are not mockable https://github.com/mockk/mockk/issues/27
        coEvery {
            postgrestBuilder.insert(
                values = any<List<Person>>(),
                upsert = false,
                onConflict = any(),
                returning = any(),
                count = any(),
                json = Json,
                filter = any(),
            )
        } returns postgrestResult

    }

    @AfterTest
    fun tearDown() {
        unmockkStatic(SupabaseClient::postgrest)
    }


    @Test
    fun testSavePerson() {

        val fakePersons = listOf(Person("name_1", 1), Person("name_2", 2))

        runBlocking {
            val result = savePerson(fakePersons, supabaseClient)
            assertEquals(2, result.size)
        }
    }
}

// currently getting ex
//kotlinx.serialization.SerializationException: Serializer for class 'Person' is not found.
//Please ensure that class is marked as '@Serializable' and that the serialization compiler plugin is applied.
//
//at kotlinx.serialization.internal.Platform_commonKt.serializerNotRegistered(Platform.common.kt:92)
//at kotlinx.serialization.internal.PlatformKt.platformSpecificSerializerNotRegistered(Platform.kt:28)
//at kotlinx.serialization.SerializersKt__SerializersKt.serializer(Serializers.kt:134)
//at kotlinx.serialization.SerializersKt.serializer(Unknown Source)
//at MainKtTest$setUp$4.invokeSuspend(MainKtTest.kt:68)
//at MainKtTest$setUp$4.invoke(MainKtTest.kt)
//at MainKtTest$setUp$4.invoke(MainKtTest.kt)
//at io.mockk.impl.eval.RecordedBlockEvaluator$record$block$2$1.invokeSuspend(RecordedBlockEvaluator.kt:27)
//at io.mockk.impl.eval.RecordedBlockEvaluator$record$block$2$1.invoke(RecordedBlockEvaluator.kt)
//at io.mockk.impl.eval.RecordedBlockEvaluator$record$block$2$1.invoke(RecordedBlockEvaluator.kt)
//at io.mockk.InternalPlatformDsl$runCoroutine$1.invokeSuspend(InternalPlatformDsl.kt:23)
//at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
//at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:106)
//at kotlinx.coroutines.EventLoopImplBase.processNextEvent(EventLoop.common.kt:280)
//at kotlinx.coroutines.BlockingCoroutine.joinBlocking(Builders.kt:85)
//at kotlinx.coroutines.BuildersKt__BuildersKt.runBlocking(Builders.kt:59)
//at kotlinx.coroutines.BuildersKt.runBlocking(Unknown Source)
//at kotlinx.coroutines.BuildersKt__BuildersKt.runBlocking$default(Builders.kt:38)
//at kotlinx.coroutines.BuildersKt.runBlocking$default(Unknown Source)
//at io.mockk.InternalPlatformDsl.runCoroutine(InternalPlatformDsl.kt:22)
//at io.mockk.impl.eval.RecordedBlockEvaluator$record$block$2.invoke(RecordedBlockEvaluator.kt:27)
//at io.mockk.impl.eval.RecordedBlockEvaluator$enhanceWithRethrow$1.invoke(RecordedBlockEvaluator.kt:76)
//at io.mockk.impl.recording.JvmAutoHinter.autoHint(JvmAutoHinter.kt:23)
//at io.mockk.impl.eval.RecordedBlockEvaluator.record(RecordedBlockEvaluator.kt:39)
//at io.mockk.impl.eval.EveryBlockEvaluator.every(EveryBlockEvaluator.kt:30)
//at io.mockk.MockKDsl.internalCoEvery(API.kt:100)
//at io.mockk.MockKKt.coEvery(MockK.kt:169)
//at MainKtTest.setUp(MainKtTest.kt:34)
//at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
//at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
//at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
//at java.lang.reflect.Method.invoke(Method.java:498)
//at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:725)
//at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)
//at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:131)
//at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:149)
//at org.junit.jupiter.engine.extension.TimeoutExtension.interceptLifecycleMethod(TimeoutExtension.java:126)
//at org.junit.jupiter.engine.extension.TimeoutExtension.interceptBeforeEachMethod(TimeoutExtension.java:76)
//at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115)
//at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)
//at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:106)
//at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:64)
//at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:45)
//at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:37)
//at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)
//at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98)
//at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeMethodInExtensionContext(ClassBasedTestDescriptor.java:506)
//at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.lambda$synthesizeBeforeEachMethodAdapter$21(ClassBasedTestDescriptor.java:491)
//at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeEachMethods$3(TestMethodTestDescriptor.java:171)
//at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeMethodsOrCallbacksUntilExceptionOccurs$6(TestMethodTestDescriptor.java:199)
//at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
//at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeMethodsOrCallbacksUntilExceptionOccurs(TestMethodTestDescriptor.java:199)
//at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeEachMethods(TestMethodTestDescriptor.java:168)
//at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:131)
//at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:66)
//at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:151)
//at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
//at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
//at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
//at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
//at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
//at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
//at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
//at java.util.ArrayList.forEach(ArrayList.java:1259)
//at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
//at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
//at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
//at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
//at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
//at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
//at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
//at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
//at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
//at java.util.ArrayList.forEach(ArrayList.java:1259)
//at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
//at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
//at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
//at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
//at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
//at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
//at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
//at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
//at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
//at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:35)
//at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)
//at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:54)
//at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:107)
//at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:88)
//at org.junit.platform.launcher.core.EngineExecutionOrchestrator.lambda$execute$0(EngineExecutionOrchestrator.java:54)
//at org.junit.platform.launcher.core.EngineExecutionOrchestrator.withInterceptedStreams(EngineExecutionOrchestrator.java:67)
//at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:52)
//at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:114)
//at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:86)
//at org.junit.platform.launcher.core.DefaultLauncherSession$DelegatingLauncher.execute(DefaultLauncherSession.java:86)
//at org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestClassProcessor$CollectAllTestClassesExecutor.processAllTestClasses(JUnitPlatformTestClassProcessor.java:110)
//at org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestClassProcessor$CollectAllTestClassesExecutor.access$000(JUnitPlatformTestClassProcessor.java:90)
//at org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestClassProcessor.stop(JUnitPlatformTestClassProcessor.java:85)
//at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.stop(SuiteTestClassProcessor.java:62)
//at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
//at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
//at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
//at java.lang.reflect.Method.invoke(Method.java:498)
//at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:36)
//at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
//at org.gradle.internal.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:33)
//at org.gradle.internal.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:94)
//at com.sun.proxy.$Proxy2.stop(Unknown Source)
//at org.gradle.api.internal.tasks.testing.worker.TestWorker$3.run(TestWorker.java:193)
//at org.gradle.api.internal.tasks.testing.worker.TestWorker.executeAndMaintainThreadName(TestWorker.java:129)
//at org.gradle.api.internal.tasks.testing.worker.TestWorker.execute(TestWorker.java:100)
//at org.gradle.api.internal.tasks.testing.worker.TestWorker.execute(TestWorker.java:60)
//at org.gradle.process.internal.worker.child.ActionExecutionWorker.execute(ActionExecutionWorker.java:56)
//at org.gradle.process.internal.worker.child.SystemApplicationClassLoaderWorker.call(SystemApplicationClassLoaderWorker.java:113)
//at org.gradle.process.internal.worker.child.SystemApplicationClassLoaderWorker.call(SystemApplicationClassLoaderWorker.java:65)
//at worker.org.gradle.process.internal.worker.GradleWorkerMain.run(GradleWorkerMain.java:69)
//at worker.org.gradle.process.internal.worker.GradleWorkerMain.main(GradleWorkerMain.java:74)
