package org.mpxj;

public interface SlackCalculator
{
   Duration calculateFreeSlack(Task task);

   Duration calculateTotalSlack(Task task);
}
