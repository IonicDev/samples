/*
 * (c) 2018 Ionic Security Inc.
 *
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html) and the
 * Privacy Policy (https://www.ionic.com/privacy-notice/).
 *
*/

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using IonicSecurity.SDK;

namespace CheckDefaultPersistor
{
    class Program
    {
        static void Main(string[] args)
        {
            Agent agent = new Agent();
            agent.Initialize();

            // Check if there are profiles.
            if (agent.HasAnyProfiles)
            {
                foreach (DeviceProfile profile in agent.AllProfiles)
                {
                    Console.WriteLine("Name: " + profile.Name + 
                                      ", Id: " + profile.DeviceId);
                }

                // Check if there is an active profile.
                if (agent.HasActiveProfile)
                {
                    Console.WriteLine("\nActive profile, Name: " + agent.ActiveProfile.Name +
                                      ", Id: " + agent.ActiveProfile.DeviceId);
                }
                else
                {
                    Console.WriteLine("There is not an active device profile selected on this device.");
                }
                
            }
            else
            {
                Console.WriteLine("There are no device profiles on this device."); 
            }

            Console.Write("\nPress Enter to continue...");
            Console.ReadKey();
        }
    }
}
