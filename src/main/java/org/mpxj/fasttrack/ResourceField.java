/*
 * file:       ResourceField.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2017
 * date:       14/03/2016
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package org.mpxj.fasttrack;

/**
 * List of columns contained in the Resource table.
 */
enum ResourceField implements FastTrackField
{
   RESOURCE_ID(1),
   RESOURCE_NAME(2),
   CATEGORY(3),
   PER_USE_COST(4),
   STANDARD_RATE(5),
   OVERTIME_RATE(6),
   WORK(7),
   TOTAL_RESOURCE_COST(8),
   _RSRCDATA(9),
   _RSRCTINFO(11),
   _RESOURCE_GUID(12),
   _RESOURCE_FILE_GUID(13),
   _RESOURCE_TIMESTAMP(14),
   _RESOURCE_VERSION(15),
   _RESOURCE_WORK_CALENDAR_ID(16),
   BASE_CALENDAR(17),
   GROUP(18),
   CODE(19),
   MATERIAL_LABEL(20),
   BUSINESS_ADDRESS_STREET(50),
   BUSINESS_ADDRESS_CITY(51),
   BUSINESS_ADDRESS_STATE(52),
   BUSINESS_ADDRESS_ZIP(53),
   BUSINESS_ADDRESS_COUNTRY(54),
   HOME_ADDRESS_STREET(55),
   HOME_ADDRESS_CITY(56),
   HOME_ADDRESS_STATE(57),
   HOME_ADDRESS_ZIP(58),
   HOME_ADDRESS_COUNTRY(59),
   RESOURCE_IMAGE(70),
   FIRST_NAME(80),
   PHONETIC_FIRST_NAME(81),
   LAST_NAME(82),
   PHONETIC_LAST_NAME(83),
   MIDDLE_NAME(84),
   PHONETIC_MIDDLE_NAME(85),
   PREFIX(86),
   SUFFIX(87),
   INITIALS(88),
   NICKNAME(89),
   COMPANY(90),
   JOB_TITLE(91),
   DEPARTMENT(92),
   EMPLOYEE_ID(93),
   MANAGER(94),
   ASSISTANT(95),
   CUSTOMER_ID(96),
   GOVERNMENT_ID(97),
   MAIN_PHONE(100),
   BUSINESS_PHONE(101),
   BUSINESS_PHONE_2(102),
   BUSINESS_FAX(103),
   HOME_PHONE(104),
   HOME_PHONE_2(105),
   HOME_FAX(106),
   MOBILE_PHONE(107),
   MOBILE_PHONE_2(108),
   PAGER(109),
   ASSISTANTS_PHONE(110),
   EMAIL_ADDRESS(120),
   EMAIL_ADDRESS_2(121),
   EMAIL_ADDRESS_3(122),
   EMAIL_ADDRESS_4(123),
   EMAIL_ADDRESS_5(124),
   EMAIL_ADDRESS_6(125),
   IM_ADDRESS(140),
   IM_ADDRESS_2(141),
   IM_ADDRESS_3(142),
   IM_ADDRESS_4(143),
   IM_ADDRESS_5(144),
   IM_ADDRESS_6(145),
   BUSINESS_URL(160),
   HOME_URL(161),
   FREE_BUSY_URL(162),
   RESOURCE_NOTES(170),
   RESOURCE_CUSTOM_TEXT(171),
   RESOURCE_CUSTOM_TEXT_2(172),
   RESOURCE_CUSTOM_TEXT_3(173),
   RESOURCE_CUSTOM_TEXT_4(174),
   NUMBER_1(250),
   NUMBER_2(251),
   NUMBER_3(252),
   NUMBER_4(253),
   NUMBER_5(254),
   NUMBER_6(255),
   NUMBER_7(256),
   NUMBER_8(257),
   NUMBER_9(258),
   NUMBER_10(259),
   NUMBER_11(260),
   NUMBER_12(261),
   NUMBER_13(262),
   NUMBER_14(263),
   NUMBER_15(264),
   NUMBER_16(265),
   NUMBER_17(266),
   NUMBER_18(267),
   NUMBER_19(268),
   NUMBER_20(269),
   NUMBER_21(270),
   NUMBER_22(271),
   NUMBER_23(272),
   NUMBER_24(273),
   NUMBER_25(274),
   NUMBER_26(275),
   NUMBER_27(276),
   NUMBER_28(277),
   NUMBER_29(278),
   NUMBER_30(279),
   NUMBER_31(280),
   NUMBER_32(281),
   NUMBER_33(282),
   NUMBER_34(283),
   NUMBER_35(284),
   NUMBER_36(285),
   NUMBER_37(286),
   NUMBER_38(287),
   NUMBER_39(288),
   NUMBER_40(289),
   NUMBER_41(290),
   NUMBER_42(291),
   NUMBER_43(292),
   NUMBER_44(293),
   NUMBER_45(294),
   NUMBER_46(295),
   NUMBER_47(296),
   NUMBER_48(297),
   NUMBER_49(298),
   NUMBER_50(299),
   TEXT_1(450),
   TEXT_2(451),
   TEXT_3(452),
   TEXT_4(453),
   TEXT_5(454),
   TEXT_6(455),
   TEXT_7(456),
   TEXT_8(457),
   TEXT_9(458),
   TEXT_10(459),
   TEXT_11(460),
   TEXT_12(461),
   TEXT_13(462),
   TEXT_14(463),
   TEXT_15(464),
   TEXT_16(465),
   TEXT_17(466),
   TEXT_18(467),
   TEXT_19(468),
   TEXT_20(469),
   TEXT_21(470),
   TEXT_22(471),
   TEXT_23(472),
   TEXT_24(473),
   TEXT_25(474),
   TEXT_26(475),
   TEXT_27(476),
   TEXT_28(477),
   TEXT_29(478),
   TEXT_30(479),
   TEXT_31(480),
   TEXT_32(481),
   TEXT_33(482),
   TEXT_34(483),
   TEXT_35(484),
   TEXT_36(485),
   TEXT_37(486),
   TEXT_38(487),
   TEXT_39(488),
   TEXT_40(489),
   TEXT_41(490),
   TEXT_42(491),
   TEXT_43(492),
   TEXT_44(493),
   TEXT_45(494),
   TEXT_46(495),
   TEXT_47(496),
   TEXT_48(497),
   TEXT_49(498),
   TEXT_50(499),
   HYPERLINK_1(650),
   HYPERLINK_2(651),
   HYPERLINK_3(652),
   HYPERLINK_4(653),
   HYPERLINK_5(654),
   HYPERLINK_6(655),
   HYPERLINK_7(656),
   HYPERLINK_8(657),
   HYPERLINK_9(658),
   HYPERLINK_10(659),
   FLAG_1(710),
   FLAG_2(711),
   FLAG_3(712),
   FLAG_4(713),
   FLAG_5(714),
   FLAG_6(715),
   FLAG_7(716),
   FLAG_8(717),
   FLAG_9(718),
   FLAG_10(719),
   FLAG_11(720),
   FLAG_12(721),
   FLAG_13(722),
   FLAG_14(723),
   FLAG_15(724),
   FLAG_16(725),
   FLAG_17(726),
   FLAG_18(727),
   FLAG_19(728),
   FLAG_20(729),
   CALCULATION_1(780),
   CALCULATION_2(781),
   CALCULATION_3(782),
   CALCULATION_4(783),
   CALCULATION_5(784),
   CALCULATION_6(785),
   CALCULATION_7(786),
   CALCULATION_8(787),
   CALCULATION_9(788),
   CALCULATION_10(789),
   CALCULATION_11(790),
   CALCULATION_12(791),
   CALCULATION_13(792),
   CALCULATION_14(793),
   CALCULATION_15(794),
   CALCULATION_16(795),
   CALCULATION_17(796),
   CALCULATION_18(797),
   CALCULATION_19(798),
   CALCULATION_20(799),
   CALCULATION_21(800),
   CALCULATION_22(801),
   CALCULATION_23(802),
   CALCULATION_24(803),
   CALCULATION_25(804),
   CALCULATION_26(805),
   CALCULATION_27(806),
   CALCULATION_28(807),
   CALCULATION_29(808),
   CALCULATION_30(809),
   CALCULATION_31(810),
   CALCULATION_32(811),
   CALCULATION_33(812),
   CALCULATION_34(813),
   CALCULATION_35(814),
   CALCULATION_36(815),
   CALCULATION_37(816),
   CALCULATION_38(817),
   CALCULATION_39(818),
   CALCULATION_40(819),
   CALCULATION_41(820),
   CALCULATION_42(821),
   CALCULATION_43(822),
   CALCULATION_44(823),
   CALCULATION_45(824),
   CALCULATION_46(825),
   CALCULATION_47(826),
   CALCULATION_48(827),
   CALCULATION_49(828),
   CALCULATION_50(829),
   CALCULATION_51(830),
   CALCULATION_52(831),
   CALCULATION_53(832),
   CALCULATION_54(833),
   CALCULATION_55(834),
   CALCULATION_56(835),
   CALCULATION_57(836),
   CALCULATION_58(837),
   CALCULATION_59(838),
   CALCULATION_60(839),
   CALCULATION_61(840),
   CALCULATION_62(841),
   CALCULATION_63(842),
   CALCULATION_64(843),
   CALCULATION_65(844),
   CALCULATION_66(845),
   CALCULATION_67(846),
   CALCULATION_68(847),
   CALCULATION_69(848),
   CALCULATION_70(849),
   CALCULATION_71(850),
   CALCULATION_72(851),
   CALCULATION_73(852),
   CALCULATION_74(853),
   CALCULATION_75(854),
   CALCULATION_76(855),
   CALCULATION_77(856),
   CALCULATION_78(857),
   CALCULATION_79(858),
   CALCULATION_80(859),
   CALCULATION_81(860),
   CALCULATION_82(861),
   CALCULATION_83(862),
   CALCULATION_84(863),
   CALCULATION_85(864),
   CALCULATION_86(865),
   CALCULATION_87(866),
   CALCULATION_88(867),
   CALCULATION_89(868),
   CALCULATION_90(869),
   CALCULATION_91(870),
   CALCULATION_92(871),
   CALCULATION_93(872),
   CALCULATION_94(873),
   CALCULATION_95(874),
   CALCULATION_96(875),
   CALCULATION_97(876),
   CALCULATION_98(877),
   CALCULATION_99(878),
   CALCULATION_100(879),
   IMAGE_1(930),
   IMAGE_2(931),
   IMAGE_3(932),
   IMAGE_4(933),
   IMAGE_5(934),
   IMAGE_6(935),
   IMAGE_7(936),
   IMAGE_8(937),
   IMAGE_9(938),
   IMAGE_10(939);

   /**
    * Constructor.
    *
    * @param value field ID from FTS file
    */
   ResourceField(int value)
   {
      m_value = value;
   }

   private final int m_value;

   /**
    * Retrieve a ResourceField instance given a field ID.
    *
    * @param value field ID
    * @return ResourceField instance
    */
   public static final ResourceField getInstance(int value)
   {
      ResourceField result;
      if (value < 0 || value >= MAP.length)
      {
         result = null;
      }
      else
      {
         result = MAP[value];
      }
      return result;
   }

   private static final ResourceField[] MAP = new ResourceField[IMAGE_10.m_value + 1];
   static
   {
      for (ResourceField field : ResourceField.values())
      {
         MAP[field.m_value] = field;
      }
   }

}
