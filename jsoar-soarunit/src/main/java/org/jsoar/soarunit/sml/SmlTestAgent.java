/*
 * Copyright (c) 2010 Dave Ray <daveray@gmail.com>
 *
 * Created on Jul 28, 2010
 */
package org.jsoar.soarunit.sml;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoar.kernel.SoarException;
import org.jsoar.soarunit.FiringCounts;
import org.jsoar.soarunit.Test;
import org.jsoar.soarunit.TestAgent;
import org.jsoar.util.FileTools;

import sml.Agent;
import sml.Agent.PrintEventInterface;
import sml.Identifier;
import sml.IntElement;
import sml.Kernel;
import sml.Kernel.UpdateEventInterface;
import sml.smlPrintEventId;

/**
 * @author ray
 */
public class SmlTestAgent implements TestAgent, PrintEventInterface,
        UpdateEventInterface
{
    private static final int TRIES = 10;
    private Kernel kernel;
    private Agent agent;
    private TestRhsFunction passFunction;
    private TestRhsFunction failFunction;
    private final StringBuilder output = new StringBuilder();

    // these used to put soarunit-specific input on the input-link
    // currently just the cycle count, which is very helpful for
    // writing some kinds of tests and being able to fail early
    private Identifier inputLink;
    private Identifier soarUnitWme;
    private IntElement cycleCountWme;

    /*
     * (non-Javadoc)
     * 
     * @see org.jsoar.soarunit.TestAgent#dispose()
     */
    @Override
    public void dispose()
    {
        output.setLength(0);
        if (agent != null)
        {
            kernel.DestroyAgent(agent);
            agent = null;
        }
        if (kernel != null)
        {
            kernel.Shutdown();
            kernel = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsoar.soarunit.TestAgent#getCycleCount()
     */
    @Override
    public long getCycleCount()
    {
        return agent.GetDecisionCycleCounter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsoar.soarunit.TestAgent#getFailMessage()
     */
    @Override
    public String getFailMessage()
    {
        return failFunction.getArguments();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsoar.soarunit.TestAgent#getFiringCounts()
     */
    @Override
    public FiringCounts getFiringCounts()
    {
        // TODO SoarUnit SML: use executeCommandLineXml to get list of firing
        // counts
        try
        {
            return extractFiringCountsFromPrintedOutput(executeCommandLine("firing-counts", false));
        }
        catch (SoarException e)
        {
            e.printStackTrace();
            return new FiringCounts();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsoar.soarunit.TestAgent#getOutput()
     */
    @Override
    public String getOutput()
    {
        return output.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsoar.soarunit.TestAgent#getPassMessage()
     */
    @Override
    public String getPassMessage()
    {
        // TODO Auto-generated method stub
        return passFunction.getArguments();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsoar.soarunit.TestAgent#initialize(org.jsoar.soarunit.Test)
     */
    @Override
    public void initialize(Test test) throws SoarException
    {
        output.setLength(0);

        kernel = Kernel.CreateKernelInCurrentThread();
        kernel.StopEventThread();
        this.commonInitialize(test);
        agent.RegisterForPrintEvent(smlPrintEventId.smlEVENT_PRINT, this, null, false);
        loadTestCode(test);
    }

    private static int getAvailablePort()
    {
        for (int tries = 0; tries < TRIES; ++tries)
        {
            // Grab an IANA ephemeral port 49152 to 65535
            Random r = new Random();
            int port = r.nextInt(65535 - 49152) + 49152;

            if (available(port))
            {
                return port;
            }
        }
        throw new RuntimeException("It's taking more than " + TRIES + " tries to find a port to use.");
    }

    private static boolean available(int port)
    {
        // http://stackoverflow.com/questions/434718/sockets-discover-port-availability-using-java
        ServerSocket ss = null;
        try
        {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            return true;
        }
        catch (IOException e)
        {
            // ignored: likely because port is already bound
        }
        finally
        {
            if (ss != null)
            {
                try
                {
                    ss.close();
                }
                catch (IOException e)
                {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }

    public void debug(Test test, boolean exitOnClose) throws SoarException
    {
        int port = getAvailablePort();
        kernel = Kernel.CreateKernelInNewThread(port);
        this.commonInitialize(test);

        // TODO SoarUnit SML: If this fails, there's really no way to tell.
        // TODO SoarUnit SML: This requires that soar/bin be on the system path
        String soarHome = System.getProperty("soar.home", null);
        // TODO SoarUnit SML: library path has to end with a slash. See
        // http://code.google.com/p/soar/issues/detail?id=82.
        if (soarHome != null && !soarHome.endsWith("\\") && !soarHome.endsWith("/"))
        {
            soarHome += File.separator;
        }
        soarHome += "SoarJavaDebugger.jar";

        System.out.println("launching debugger on port " + port + " from: " + soarHome);
        boolean success = agent.SpawnDebugger(port, soarHome);
        if (success)
        {
            System.out.println("successfully launched debugger");
        }
        else
        {
            System.out.println("failed to launch debugger; check that SOAR_HOME is set properly");
        }

        loadTestCode(test);

        // TODO SoarUnit SML: How do we clean up? Detect debugger detach?
        // There are several kernel functions for getting connection status,
        // etc.,
        // so in principle debugger detach can be detected. But it's not clear
        // when we should check (there is not an event for detecting when a
        // connection closes, changes, etc.). Maybe we should just set up a
        // polling function here.
    }

    /**
     * creates the agent, sets up update event, RHS functions assumes kernel has
     * already been created
     * 
     * @param test
     */
    private void commonInitialize(Test test)
    {
        initializeRhsFunctions();
        this.kernel.RegisterForUpdateEvent(
                sml.smlUpdateEventId.smlEVENT_AFTER_ALL_OUTPUT_PHASES, this,
                null);
        this.agent = kernel.CreateAgent(test.getName());
        this.inputLink = agent.GetInputLink();
        this.soarUnitWme = this.inputLink.CreateIdWME("soar-unit");
        this.cycleCountWme = this.soarUnitWme.CreateIntWME("cycle-count",this.getCycleCount());
    }

    private void initializeRhsFunctions()
    {
        passFunction = TestRhsFunction.addTestFunction(kernel, "pass");
        failFunction = TestRhsFunction.addTestFunction(kernel, "fail");
    }

    private void loadTestCode(Test test) throws SoarException
    {
        executeCommandLine(String.format("pushd \"%s\"", 
                FileTools.getParent(test.getTestCase().getFile()).replace('\\', '/')), true);
        executeCommandLine(prepSoarCodeForSml(test.getTestCase().getSetup()), true);
        executeCommandLine(prepSoarCodeForSml(test.getContent()), true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsoar.soarunit.TestAgent#isFailCalled()
     */
    @Override
    public boolean isFailCalled()
    {
        return failFunction.isCalled();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsoar.soarunit.TestAgent#isPassCalled()
     */
    @Override
    public boolean isPassCalled()
    {
        return passFunction.isCalled();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsoar.soarunit.TestAgent#printMatchesOnFailure()
     */
    @Override
    public void printMatchesOnFailure()
    {
        output.append("\n# Matches for pass rules #\n");
        // TODO use executeCommandLineXml to get list of user rules and print
        // matches output.append("\n" + executeCommandLine("matches pass"));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jsoar.soarunit.TestAgent#run()
     */
    @Override
    public void run()
    {
        agent.RunSelf(50000);
    }

    private String executeCommandLine(String code, boolean echo)
            throws SoarException
    {
        final String result = agent.ExecuteCommandLine(code, echo);
        if (!agent.GetLastCommandLineResult())
        {
            throw new SoarException(result);
        }
        return result;
    }

    private String prepSoarCodeForSml(String code)
    {
        return code.replace("(pass)", "(exec pass)") // when there are no args
                .replace("(fail)", "(exec fail)")
                .replace("(pass ", "(exec pass ")    // when there are args
                .replace("(fail ", "(exec fail ");
    }

    @Override
    public void printEventHandler(int eventID, Object data, Agent agent,
            String message)
    {
        output.append(message);
        // System.out.print(message);
        // System.out.flush();
    }

    static FiringCounts extractFiringCountsFromPrintedOutput(String in)
    {
        final FiringCounts result = new FiringCounts();
        final Pattern pattern = Pattern.compile("^\\s*(\\d+):\\s*(.*)$", Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(in);
        while (matcher.find())
        {
            result.adjust(matcher.group(2), Long.parseLong(matcher.group(1)));
        }
        return result;
    }

    @Override
    public void updateEventHandler(int arg0, Object arg1, Kernel arg2, int arg3)
    {
        // update the cycle count
        this.cycleCountWme.Update(this.getCycleCount());
    }
}
