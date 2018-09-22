package br.ufu;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static br.ufu.TestUtil.*;
import static java.math.BigInteger.ONE;
import static java.nio.charset.Charset.defaultCharset;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;

public class Caso01UpdateTest {

    @Test
    public void shouldUptadeItem() throws Exception {

        //Dado: Criei as variáveis
        File tempLogFile = File.createTempFile("test_", ".log");
        String[] commands = getArgs(tempLogFile, 4462);

        Server serverSpy = Mockito.spy(new Server(commands));
        Client clientSpy = Mockito.spy(new Client(commands));

        Scanner mockScanner = Mockito.mock(Scanner.class);

        List<String> inputs = new ArrayList<>();
        inputs.add("CREATE 1 I");
        inputs.add("UPDATE 1 J");
        inputs.add("sair");

        final int[] currentInput = {0};

        //Mockei com spy para simular o input do usuario
        //Também poderei usar estas classes depois
        Mockito.when(clientSpy.getScanner()).thenReturn(mockScanner);
        Mockito.when(mockScanner.hasNext()).thenReturn(true);
        Mockito.when(mockScanner.nextLine()).thenAnswer((Answer<String>) invocation -> inputs.get(currentInput[0]++));


        //Start das Threads
        Thread tServer = getThread(serverSpy);
        tServer.start();

        await().untilAsserted(() -> {
            Thread tClient = getThread(clientSpy);
            tClient.start();

            tClient.join();

            //O Arquivo de Log deve ser escrito
            assertEquals(
                    splitCommads("CREATE 1 I", "UPDATE 1 J"),
                    readFileToString(tempLogFile, defaultCharset())
            );

            //No banco o registro deve estar com o valor atualizado
            assertEquals("J", serverSpy.getCrudRepository().read(ONE));
        });

        tServer.stop();
    }


}
