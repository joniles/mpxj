Attribute VB_Name = "GenerateTestData"

Sub GenerateAll()
    GenerateTaskCustomFlags
    GenerateTaskCustomNumbers
    GenerateTaskCustomDurations
    GenerateTaskLinks
    GenerateTaskCustomDates
    GenerateTaskCustomStarts
    GenerateTaskCustomFinishes
    GenerateTaskCustomCosts
    GenerateTaskCustomText
    GenerateTaskCustomOutlineCodes
End Sub

Sub NameThatField(value As Long)
    Dim name As String
    name = FieldConstantToFieldName(value)
    Debug.Print value & "=" & name
End Sub

'
' Takes a field name and an array of values for that field
' Create a task per value and set the named field to that value
'
Sub AddTasksWithFieldValues(fieldName As String, Vals As Variant)

    Dim index As Integer
    Dim offset As Integer
    
    If LBound(Vals) = 0 Then
        offset = 1
    Else
        offset = 0
    End If
    
    For index = LBound(Vals) To UBound(Vals)
        
        Dim taskName As String
        taskName = fieldName & " - " & "Task " & index
        
        Dim task As task
        Set task = ActiveProject.Tasks.Add(taskName)
        
        SetTaskField Field:=fieldName, value:=Vals(index), TaskID:=task.ID
    Next

End Sub


Sub AddTasksWithCustomFieldValues(FieldNamePrefix As String, Vals As Variant)

    Dim index As Integer
    Dim offset As Integer
    
    If LBound(Vals) = 0 Then
        offset = 1
    Else
        offset = 0
    End If
    
    For index = LBound(Vals) To UBound(Vals)
        Dim fieldName As String
        fieldName = FieldNamePrefix & (index + offset)
        
        Dim task As task
        Set task = ActiveProject.Tasks.Add(fieldName)        
        SetTaskField Field:=fieldName, value:=Vals(index), TaskID:=task.ID
                                      
        AddTaskColumn fieldName                                        
    Next

End Sub
    
Sub AddTaskColumn (fieldName as String)
    Dim tableName As String       
    tableName = ActiveProject.TaskTableList.Item(1)        
    SelectTaskColumn Column:="Resource Names"            
    TableEdit name:=tableName, TaskTable:=True, NewFieldName:=fieldName
    TableApply name:=tableName
End Sub

Sub GenerateTaskCustomFlags()

    Dim Vals As Variant
    
    Vals = Array("Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes")
    
    FileNew SummaryInfo:=False
    
    AddTasksWithCustomFieldValues "Flag", Vals
    
    SaveFiles "task-flags"
    
    FileClose pjDoNotSave
            
End Sub

Sub GenerateTaskCustomNumbers()

    Dim Vals As Variant
    
    Vals = Array("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20")
    
    FileNew SummaryInfo:=False
    
    AddTasksWithCustomFieldValues "Number", Vals
    
    SaveFiles "task-numbers"
            
    FileClose pjDoNotSave
    
End Sub

Sub GenerateTaskCustomDurations()
                
    FileNew SummaryInfo:=False
    
    Dim Vals As Variant

    '
    ' Verify that we're reading the duration value correctly for each field
    '
    Vals = Array("1d", "2d", "3d", "4d", "5d", "6d", "7d", "8d", "9d", "10d")
    AddTasksWithCustomFieldValues "Duration", Vals
    
    '
    ' Verify that we're reading the duration units correctly for each field
    '
    If Application.Version = "8.0" Then
        Vals = Array("1m", "1h", "1d", "1w", "1em", "1eh", "1ed", "1ew")
    Else
        Vals = Array("1m", "1h", "1d", "1w", "1mo", "1em", "1eh", "1ed", "1ew", "1emo")
    End If
    
    Dim index As Integer
    For index = 1 To 10
        AddTasksWithFieldValues "Duration" & index, Vals
    Next

    
    SaveFiles "task-durations"
    
    FileClose pjDoNotSave
                
End Sub

'
' Note that the custom date fields are not supported by the MPXfile format
'
Sub GenerateTaskCustomDates()

    Dim Vals As Variant
    
    Vals = Array("01/01/2014 09:00", "02/01/2014 10:00", "03/01/2014 11:00", "04/01/2014 12:00", "05/01/2014 13:00", "06/01/2014 14:00", "07/01/2014 15:00", "08/01/2014 16:00", "09/01/2014 17:00", "10/01/2014 18:00")
    
    FileNew SummaryInfo:=False
    
    AddTasksWithCustomFieldValues "Date", Vals
    
    SaveFiles "task-dates"
            
    FileClose pjDoNotSave
    
End Sub


Sub GenerateTaskCustomStarts()

    Dim Vals As Variant
    
    Vals = Array("01/01/2014 09:00", "02/01/2014 10:00", "03/01/2014 11:00", "04/01/2014 12:00", "05/01/2014 13:00", "06/01/2014 14:00", "07/01/2014 15:00", "08/01/2014 16:00", "09/01/2014 17:00", "10/01/2014 18:00")
    
    FileNew SummaryInfo:=False
    
    AddTasksWithCustomFieldValues "Start", Vals
    
    SaveFiles "task-starts"
            
    FileClose pjDoNotSave
    
End Sub

Sub GenerateTaskCustomFinishes()

    Dim Vals As Variant
    
    Vals = Array("01/01/2014 09:00", "02/01/2014 10:00", "03/01/2014 11:00", "04/01/2014 12:00", "05/01/2014 13:00", "06/01/2014 14:00", "07/01/2014 15:00", "08/01/2014 16:00", "09/01/2014 17:00", "10/01/2014 18:00")
    
    FileNew SummaryInfo:=False
    
    AddTasksWithCustomFieldValues "Finish", Vals
    
    SaveFiles "task-finishes"
            
    FileClose pjDoNotSave
    
End Sub

Sub GenerateTaskCustomCosts()

    Dim Vals As Variant
    
    Vals = Array("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    
    FileNew SummaryInfo:=False
    
    AddTasksWithCustomFieldValues "Cost", Vals
    
    SaveFiles "task-costs"
            
    FileClose pjDoNotSave
    
End Sub


Sub GenerateTaskCustomText()

    Dim Vals As Variant
    
    Vals = Array("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30")
    
    FileNew SummaryInfo:=False
    
    AddTasksWithCustomFieldValues "Text", Vals
    
    SaveFiles "task-text"
            
    FileClose pjDoNotSave
    
End Sub


Sub GenerateTaskCustomOutlineCodes()
        
    Dim Vals As Variant
            
    FileNew SummaryInfo:=False

    '
    ' Add the values we want to enter to the lookup table
    '
    Application.LookUpTableAdd pjCustomTaskOutlineCode1, Level:=1, Code:="OC1A"
    Application.LookUpTableAdd pjCustomTaskOutlineCode1, Level:=2, Code:="OC1B"
    Application.LookUpTableAdd pjCustomTaskOutlineCode2, Level:=1, Code:="OC2A"
    Application.LookUpTableAdd pjCustomTaskOutlineCode2, Level:=2, Code:="OC2B"
    Application.LookUpTableAdd pjCustomTaskOutlineCode3, Level:=1, Code:="OC3A"
    Application.LookUpTableAdd pjCustomTaskOutlineCode3, Level:=2, Code:="OC3B"
    Application.LookUpTableAdd pjCustomTaskOutlineCode4, Level:=1, Code:="OC4A"
    Application.LookUpTableAdd pjCustomTaskOutlineCode4, Level:=2, Code:="OC4B"
    Application.LookUpTableAdd pjCustomTaskOutlineCode5, Level:=1, Code:="OC5A"
    Application.LookUpTableAdd pjCustomTaskOutlineCode5, Level:=2, Code:="OC5B"
    Application.LookUpTableAdd pjCustomTaskOutlineCode6, Level:=1, Code:="OC6A"
    Application.LookUpTableAdd pjCustomTaskOutlineCode6, Level:=2, Code:="OC6B"
    Application.LookUpTableAdd pjCustomTaskOutlineCode7, Level:=1, Code:="OC7A"
    Application.LookUpTableAdd pjCustomTaskOutlineCode7, Level:=2, Code:="OC7B"
    Application.LookUpTableAdd pjCustomTaskOutlineCode8, Level:=1, Code:="OC8A"
    Application.LookUpTableAdd pjCustomTaskOutlineCode8, Level:=2, Code:="OC8B"
    Application.LookUpTableAdd pjCustomTaskOutlineCode9, Level:=1, Code:="OC9A"
    Application.LookUpTableAdd pjCustomTaskOutlineCode9, Level:=2, Code:="OC9B"
    Application.LookUpTableAdd pjCustomTaskOutlineCode10, Level:=1, Code:="OC10A"
    Application.LookUpTableAdd pjCustomTaskOutlineCode10, Level:=2, Code:="OC10B"
    
    CustomOutlineCodeEdit FieldID:=pjCustomTaskOutlineCode1, Level:=1, Sequence:=pjCustomOutlineCodeCharacters, Length:="Any", Separator:="."
    CustomOutlineCodeEdit FieldID:=pjCustomTaskOutlineCode2, Level:=1, Sequence:=pjCustomOutlineCodeCharacters, Length:="Any", Separator:="."
    CustomOutlineCodeEdit FieldID:=pjCustomTaskOutlineCode3, Level:=1, Sequence:=pjCustomOutlineCodeCharacters, Length:="Any", Separator:="."
    CustomOutlineCodeEdit FieldID:=pjCustomTaskOutlineCode4, Level:=1, Sequence:=pjCustomOutlineCodeCharacters, Length:="Any", Separator:="."
    CustomOutlineCodeEdit FieldID:=pjCustomTaskOutlineCode5, Level:=1, Sequence:=pjCustomOutlineCodeCharacters, Length:="Any", Separator:="."
    CustomOutlineCodeEdit FieldID:=pjCustomTaskOutlineCode6, Level:=1, Sequence:=pjCustomOutlineCodeCharacters, Length:="Any", Separator:="."
    CustomOutlineCodeEdit FieldID:=pjCustomTaskOutlineCode7, Level:=1, Sequence:=pjCustomOutlineCodeCharacters, Length:="Any", Separator:="."
    CustomOutlineCodeEdit FieldID:=pjCustomTaskOutlineCode8, Level:=1, Sequence:=pjCustomOutlineCodeCharacters, Length:="Any", Separator:="."
    CustomOutlineCodeEdit FieldID:=pjCustomTaskOutlineCode9, Level:=1, Sequence:=pjCustomOutlineCodeCharacters, Length:="Any", Separator:="."
    CustomOutlineCodeEdit FieldID:=pjCustomTaskOutlineCode10, Level:=1, Sequence:=pjCustomOutlineCodeCharacters, Length:="Any", Separator:="."

    CustomOutlineCodeEdit FieldID:=pjCustomTaskOutlineCode1, Level:=2, Sequence:=pjCustomOutlineCodeCharacters, Length:="Any", Separator:="."
    CustomOutlineCodeEdit FieldID:=pjCustomTaskOutlineCode2, Level:=2, Sequence:=pjCustomOutlineCodeCharacters, Length:="Any", Separator:="."
    CustomOutlineCodeEdit FieldID:=pjCustomTaskOutlineCode3, Level:=2, Sequence:=pjCustomOutlineCodeCharacters, Length:="Any", Separator:="."
    CustomOutlineCodeEdit FieldID:=pjCustomTaskOutlineCode4, Level:=2, Sequence:=pjCustomOutlineCodeCharacters, Length:="Any", Separator:="."
    CustomOutlineCodeEdit FieldID:=pjCustomTaskOutlineCode5, Level:=2, Sequence:=pjCustomOutlineCodeCharacters, Length:="Any", Separator:="."
    CustomOutlineCodeEdit FieldID:=pjCustomTaskOutlineCode6, Level:=2, Sequence:=pjCustomOutlineCodeCharacters, Length:="Any", Separator:="."
    CustomOutlineCodeEdit FieldID:=pjCustomTaskOutlineCode7, Level:=2, Sequence:=pjCustomOutlineCodeCharacters, Length:="Any", Separator:="."
    CustomOutlineCodeEdit FieldID:=pjCustomTaskOutlineCode8, Level:=2, Sequence:=pjCustomOutlineCodeCharacters, Length:="Any", Separator:="."
    CustomOutlineCodeEdit FieldID:=pjCustomTaskOutlineCode9, Level:=2, Sequence:=pjCustomOutlineCodeCharacters, Length:="Any", Separator:="."
    CustomOutlineCodeEdit FieldID:=pjCustomTaskOutlineCode10, Level:=2, Sequence:=pjCustomOutlineCodeCharacters, Length:="Any", Separator:="."

    Vals = Array("OC1A", "OC2A", "OC3A", "OC4A", "OC5A", "OC6A", "OC7A", "OC8A", "OC9A", "OC10A")
    AddTasksWithCustomFieldValues "Outline Code", Vals

    Vals = Array("OC1A.OC1B", "OC2A.OC2B", "OC3A.OC3B", "OC4A.OC4B", "OC5A.OC5B", "OC6A.OC6B", "OC7A.OC7B", "OC8A.OC8B", "OC9A.OC9B", "OC10A.OC10B")
    AddTasksWithCustomFieldValues "Outline Code", Vals
    
    SaveFiles "task-outlinecodes"
            
    FileClose pjDoNotSave
    
End Sub


Sub GenerateTaskLinks()

    FileNew SummaryInfo:=False

    Dim task1 As task
    Dim task2 As task
    
    Set task1 = ActiveProject.Tasks.Add("Task 1")
    Set task2 = ActiveProject.Tasks.Add("Task 2")
    LinkTasksEdit From:=task1.ID, To:=task2.ID, Type:=1, Lag:="0d"
    
    Set task1 = ActiveProject.Tasks.Add("Task 1")
    Set task2 = ActiveProject.Tasks.Add("Task 2")
    LinkTasksEdit From:=task1.ID, To:=task2.ID, Type:=1, Lag:="1d"

    Set task1 = ActiveProject.Tasks.Add("Task 1")
    Set task2 = ActiveProject.Tasks.Add("Task 2")
    LinkTasksEdit From:=task1.ID, To:=task2.ID, Type:=1, Lag:="2d"

    Set task1 = ActiveProject.Tasks.Add("Task 1")
    Set task2 = ActiveProject.Tasks.Add("Task 2")
    LinkTasksEdit From:=task1.ID, To:=task2.ID, Type:=1, Lag:="1w"

    Set task1 = ActiveProject.Tasks.Add("Task 1")
    Set task2 = ActiveProject.Tasks.Add("Task 2")
    LinkTasksEdit From:=task1.ID, To:=task2.ID, Type:=1, Lag:="2w"
    
    Set task1 = ActiveProject.Tasks.Add("Task 1")
    Set task2 = ActiveProject.Tasks.Add("Task 2")
    LinkTasksEdit From:=task1.ID, To:=task2.ID, Type:=2, Lag:="2d"
    
    Set task1 = ActiveProject.Tasks.Add("Task 1")
    Set task2 = ActiveProject.Tasks.Add("Task 2")
    LinkTasksEdit From:=task1.ID, To:=task2.ID, Type:=3, Lag:="2d"

    Set task1 = ActiveProject.Tasks.Add("Task 1")
    Set task2 = ActiveProject.Tasks.Add("Task 2")
    LinkTasksEdit From:=task1.ID, To:=task2.ID, Type:=0, Lag:="2d"
    
    SaveFiles "task-links"
    
    FileClose pjDoNotSave


End Sub



Sub SaveFiles(FilenameBase As String)

    Dim parentDirectory As String
    
    parentDirectory = "generated"
    
    If Dir(parentDirectory, vbDirectory) = "" Then
        MkDir parentDirectory
    End If

    If Dir(parentDirectory & "\" & FilenameBase, vbDirectory) = "" Then
        MkDir parentDirectory & "\" & FilenameBase
    End If

    Dim Filename As String
    Filename = parentDirectory & "\" & FilenameBase & "\" & FilenameBase
    
    Select Case Application.Version
        ' Project 2013
        Case "15.0"
            CalculateAll
            FileSaveAs name:=Filename & "-project2013-mpp14.mpp", FormatID:="MSProject.MPP"
        
            CalculateAll
            FileSaveAs name:=Filename & "-project2013-mpp12.mpp", FormatID:="MSProject.MPP.12"
        
            CalculateAll
            FileSaveAs name:=Filename & "-project2013-mpp9.mpp", FormatID:="MSProject.MPP.9"
            
            CalculateAll
            FileSaveAs name:=Filename & "-project2013-mspdi.xml", FormatID:="MSProject.XML"
                        
        ' Project 2010
        Case "14.0"
            CalculateAll
            FileSaveAs name:=Filename & "-project2010-mpp14.mpp", FormatID:="MSProject.MPP"
        
            CalculateAll
            FileSaveAs name:=Filename & "-project2010-mpp12.mpp", FormatID:="MSProject.MPP.12"
        
            CalculateAll
            FileSaveAs name:=Filename & "-project2010-mpp9.mpp", FormatID:="MSProject.MPP.9"

            CalculateAll
            FileSaveAs name:=Filename & "-project2010-mspdi.xml", FormatID:="MSProject.XML"
                        
        ' Project 2007
        Case "12.0"
            CalculateAll
            FileSaveAs name:=Filename & "-project2007-mpp12.mpp", FormatID:="MSProject.MPP.12"
    
            CalculateAll
            FileSaveAs name:=Filename & "-project2007-mpp9.mpp", FormatID:="MSProject.MPP.9"
        
            CalculateAll
            FileSaveAs name:=Filename & "-project2007-mspdi.xml", FormatID:="MSProject.XML"

        ' Project 2003
        Case "11.0"
            CalculateAll
            FileSaveAs name:=Filename & "-project2003-mpp9.mpp", FormatID:="MSProject.MPP.9"
    
            'CalculateAll
            'FileSaveAs name:=Filename & "-project2003-mpp8.mpp", FormatID:="MSProject.MPP.8"
        
            CalculateAll
            FileSaveAs name:=Filename & "-project2003-mspdi.xml", FormatID:="MSProject.XML"
                        
            CalculateAll
            FileSaveAs name:="<" & Filename & "-project2003-mpd9.mpd>\" & FilenameBase & "-project2003-mpd9", FormatID:="MSProject.MPD.9"

        ' Project 2000
        Case "9.0"
            CalculateAll
            FileSaveAs name:=Filename & "-project2000-mpp9.mpp", FormatID:="MSProject.MPP.9"
    
            'CalculateAll
            'FileSaveAs name:=Filename & "-project2000-mpp8.mpp", FormatID:="MSProject.MPP.8"
                                
            CalculateAll
            FileSaveAs name:="<" & Filename & "-project2000-mpd9.mpd>\" & FilenameBase & "-project2000-mpd9", FormatID:="MSProject.MPD.9"
                        
        ' Project 98
        Case "8.0"
            'CalculateAll
            'FileSaveAs name:=Filename & "-project98-mpp8.mpp", FormatID:="MSProject.MPP.8"
                
            CalculateAll
            FileSaveAs name:=Filename & "-project98.mpx", FormatID:="MSProject.MPX.8"

            'CalculateAll
            'FileSaveAs name:="<" & Filename & "-project98-mpd8.mpd>\" & FilenameBase & "-project98-mpd8", FormatID:="MSProject.MPD.8"
                
        Case Else
            Debug.Print "Unknown Version"
            
    End Select
End Sub

