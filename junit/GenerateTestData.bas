Attribute VB_Name = "GenerateTestData"

Sub GenerateAll()
    GenerateTaskCustomFlags
    GenerateTaskCustomNumbers
    GenerateTaskCustomDurations
    GenerateTaskLinks
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
        
        'Dim fieldConstant As Long
        'fieldConstant = FieldNameToFieldConstant(fieldName, pjTask)
        'task.SetField fieldConstant, Vals(index)
        
        SetTaskField Field:=fieldName, value:=Vals(index), TaskID:=task.ID
        
        
        'SelectTaskColumn Column:="Add New Column"
        'TableEditEx name:="&Entry", TaskTable:=True, NewFieldName:=fieldName
        'TableApply name:="&Entry"
                        
        Dim tableName As String
        'Dim fields As TableFields
        'Dim lastColumnIndex As Integer
        'Dim lastColumnTitle As String
        
        'tableName = ActiveProject.TaskTables.Item(1).name
        tableName = ActiveProject.TaskTableList.Item(1)
        
        'Set fields = ActiveProject.TaskTables.Item(1).TableFields
        'lastColumnIndex = fields.Count - 1
        'lastColumnTitle = FieldConstantToFieldName(fields.Item(lastColumnIndex).Field)
        
                        
        'SelectTaskColumn Column:=lastColumnTitle
            
        SelectTaskColumn Column:="Resource Names"
            
        TableEdit name:=tableName, TaskTable:=True, NewFieldName:=fieldName
        TableApply name:=tableName
        
    Next

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
    
            CalculateAll
            FileSaveAs name:=Filename & "-project2003-mpp8.mpp", FormatID:="MSProject.MPP.8"
        
            CalculateAll
            FileSaveAs name:=Filename & "-project2003-mspdi.xml", FormatID:="MSProject.XML"
                        
            CalculateAll
            FileSaveAs name:="<" & Filename & "-project2003-mpd9.mpd>\" & FilenameBase & "-project2003-mpd9", FormatID:="MSProject.MPD.9"

        ' Project 2000
        Case "9.0"
            CalculateAll
            FileSaveAs name:=Filename & "-project2000-mpp9.mpp", FormatID:="MSProject.MPP.9"
    
            CalculateAll
            FileSaveAs name:=Filename & "-project2000-mpp8.mpp", FormatID:="MSProject.MPP.8"
                                
            CalculateAll
            FileSaveAs name:="<" & Filename & "-project2000-mpd9.mpd>\" & FilenameBase & "-project2000-mpd9", FormatID:="MSProject.MPD.9"
                        
        ' Project 98
        Case "8.0"
            CalculateAll
            FileSaveAs name:=Filename & "-project98-mpp8.mpp", FormatID:="MSProject.MPP.8"
                
            CalculateAll
            FileSaveAs name:=Filename & "-project98.mpx", FormatID:="MSProject.MPX.8"

            ' Note that MPXJ doesn't currently read MPD8 files, so we prefix the file names with X so the unit tests don't read these files
            ' We may add support for MPD8 at a later date - so it is useful to have these files to hand
            CalculateAll
            FileSaveAs name:="<" & Filename & "-project98-mpd8.mpd>\" & FilenameBase & "-project98-mpd8", FormatID:="MSProject.MPD.8"
                
        Case Else
            Debug.Print "Unknown Version"
            
    End Select
End Sub

