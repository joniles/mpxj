/*
 * file:       ActivityField.java
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
 * List of columns contained in the Activity table.
 */
enum ActivityField implements FastTrackField
{
   _ADATA(1),
   ACTIVITY_NAME(2),
   _FINFO(3),
   _ITMVISORDTBL(4),
   _COMPACTSTBL(5),
   _SUMBARYOFFTBL(6),
   NOTES(9),
   ACTIVITY_ROW_ID(10),
   ACTIVITY_ROW_NUMBER(11),
   _ACTIVITY_FILE_GUID(12),
   _ACTIVITY_GUID(13),
   _ACTIVITY_TIMESTAMP(14),
   _ACTIVITY_VERSION(15),
   WBS(16),
   PARENT_TREE(17),
   SUBPROJECT_ACTIVITY_ROW_ID(18),
   SUBPROJECT_WBS(19),
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
   TEXT_51(500),
   TEXT_52(501),
   TEXT_53(502),
   TEXT_54(503),
   TEXT_55(504),
   TEXT_56(505),
   TEXT_57(506),
   TEXT_58(507),
   TEXT_59(508),
   TEXT_60(509),
   TEXT_61(510),
   TEXT_62(511),
   TEXT_63(512),
   TEXT_64(513),
   TEXT_65(514),
   TEXT_66(515),
   TEXT_67(516),
   TEXT_68(517),
   TEXT_69(518),
   TEXT_70(519),
   TEXT_71(520),
   TEXT_72(521),
   TEXT_73(522),
   TEXT_74(523),
   TEXT_75(524),
   TEXT_76(525),
   TEXT_77(526),
   TEXT_78(527),
   TEXT_79(528),
   TEXT_80(529),
   TEXT_81(530),
   TEXT_82(531),
   TEXT_83(532),
   TEXT_84(533),
   TEXT_85(534),
   TEXT_86(535),
   TEXT_87(536),
   TEXT_88(537),
   TEXT_89(538),
   TEXT_90(539),
   TEXT_91(540),
   TEXT_92(541),
   TEXT_93(542),
   TEXT_94(543),
   TEXT_95(544),
   TEXT_96(545),
   TEXT_97(546),
   TEXT_98(547),
   TEXT_99(548),
   TEXT_100(549),
   HYPERLINK_1(700),
   HYPERLINK_2(701),
   HYPERLINK_3(702),
   HYPERLINK_4(703),
   HYPERLINK_5(704),
   HYPERLINK_6(705),
   HYPERLINK_7(706),
   HYPERLINK_8(707),
   HYPERLINK_9(708),
   HYPERLINK_10(709),
   FLAG_1(760),
   FLAG_2(761),
   FLAG_3(762),
   FLAG_4(763),
   FLAG_5(764),
   FLAG_6(765),
   FLAG_7(766),
   FLAG_8(767),
   FLAG_9(768),
   FLAG_10(769),
   FLAG_11(770),
   FLAG_12(771),
   FLAG_13(772),
   FLAG_14(773),
   FLAG_15(774),
   FLAG_16(775),
   FLAG_17(776),
   FLAG_18(777),
   FLAG_19(778),
   FLAG_20(779),
   IMAGE_1(1880),
   IMAGE_2(1881),
   IMAGE_3(1882),
   IMAGE_4(1883),
   IMAGE_5(1884),
   IMAGE_6(1885),
   IMAGE_7(1886),
   IMAGE_8(1887),
   IMAGE_9(1888),
   IMAGE_10(1889);

   /**
    * Constructor.
    *
    * @param value field ID from FTS file
    */
   ActivityField(int value)
   {
      m_value = value;
   }

   private final int m_value;

   /**
    * Retrieve an ActivityField instance given a field ID.
    *
    * @param value field ID
    * @return ActivityField instance
    */
   public static final ActivityField getInstance(int value)
   {
      ActivityField result;
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

   private static final ActivityField[] MAP = new ActivityField[IMAGE_10.m_value + 1];
   static
   {
      for (ActivityField field : ActivityField.values())
      {
         MAP[field.m_value] = field;
      }
   }

}
