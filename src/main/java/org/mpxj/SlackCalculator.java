package org.mpxj;

public interface SlackCalculator
{
   Duration calculateStartSlack(Task task);

   Duration calculateFinishSlack(Task task);

   Duration calculateFreeSlack(Task task);

   Duration calculateTotalSlack(Task task);
}
