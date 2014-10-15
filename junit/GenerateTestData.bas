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

Sub PopulateTaskFields(FieldNamePrefix As String, Vals As Variant)

    Dim index As Integer
    
    For index = LBound(Vals) To UBound(Vals)
        Dim fieldName As String
        fieldName = FieldNamePrefix & (index + 1)
        
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
    
    PopulateTaskFields "Flag", Vals
    
    SaveFiles "task-flags"
    
    FileClose pjDoNotSave
            
End Sub

Sub GenerateTaskCustomNumbers()

    Dim Vals As Variant
    
    Vals = Array("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20")
    
    FileNew SummaryInfo:=False
    
    PopulateTaskFields "Number", Vals
    
    SaveFiles "task-numbers"
            
    FileClose pjDoNotSave
    
End Sub

Sub GenerateTaskCustomDurations()

    Dim Vals As Variant
    
    Vals = Array("1d", "2d", "3d", "4d", "5d", "6d", "7d", "8d", "9d", "10d")
    
    FileNew SummaryInfo:=False
    
    PopulateTaskFields "Duration", Vals
    
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

    If Dir("data", vbDirectory) = "" Then
        MkDir "data"
    End If

    If Dir("data\" & FilenameBase, vbDirectory) = "" Then
        MkDir "data\" & FilenameBase
    End If

    Dim Filename As String
    Filename = "data\" & FilenameBase & "\" & FilenameBase
    
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
                        
        ' Project 98
        Case "8.0"
            CalculateAll
            FileSaveAs name:=Filename & "-project98-mpp8.mpp", FormatID:="MSProject.MPP.8"
                
            CalculateAll
            FileSaveAs name:=Filename & "-project98.mpx", FormatID:="MSProject.MPX.8"

            CalculateAll
            FileSaveAs name:="<" & Filename & "-project98.mpd>\" & FilenameBase & "-project98", FormatID:="MSProject.MPD.8"
                
        Case Else
            Debug.Print "Unknown Version"
            
    End Select
End Sub

