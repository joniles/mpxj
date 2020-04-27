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
    GenerateTaskTextValues
    GenerateProjectProperties
    GenerateBaselines
    GenerateProjectValueLists
    GenerateCalendars
    GenerateResourceAssignments
    GenerateResourceAssignmentFlags
    GenerateResourceAssignmentText
    GenerateResources
    GenerateResourceCustomFlags
    GenerateResourceCustomNumbers
    GenerateResourceCustomText
    GenerateResourceTypes
    GenerateDataLinks
End Sub

Sub GenerateXmlVersions()
    Dim prefix As String
    prefix = Environ("MPXJ_PRIVATE") & "\data\MPP\"
    
    Dim files() As String
    ReDim files(1000)
    Dim fileIndex As Long
    
    file = Dir(prefix & "*.mpp")
    Do Until file = ""
        files(fileIndex) = file
        fileIndex = fileIndex + 1
        file = Dir
    Loop
    
    ReDim Preserve files(fileIndex - 1)
    
    For fileIndex = 0 To UBound(files)
        file = files(fileIndex)
        xmlFile = prefix & file & ".xml"
        If Len(Dir(xmlFile)) = 0 Then
            Debug.Print "Processing: " & file
            FileOpenEx Name:=prefix & file, ReadOnly:=True, NoAuto:=True, openPool:=pjDoNotOpenPool
            FileSaveAs Name:=xmlFile, FormatID:="MSProject.XML"
            FileCloseEx pjDoNotSave
            Debug.Print "Done."
        End If
    Next fileIndex
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

Sub AddResourcesWithCustomFieldValues(FieldNamePrefix As String, Vals As Variant)

    ViewApply name:="Resource &Sheet"

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
        'Dim fieldID As Long
        'fieldID = FieldNameToFieldConstant(fieldName, pjResource)

        Dim resource As resource
        Set resource = ActiveProject.Resources.Add(fieldName)

        SetResourceField Field:=fieldName, value:=Vals(index), ResourceID:=resource.ID
        'SetResourceField fieldName, Vals(index), False, False, resource.ID
        'resource.SetField fieldID, Vals(index)

        AddResourceColumn fieldName
    Next

End Sub

Sub AddTasksWithBaselineFieldValues(fieldName As String, Vals As Variant)

    Dim index As Integer
    Dim offset As Integer

    If LBound(Vals) = 0 Then
        offset = 1
    Else
        offset = 0
    End If

    For index = LBound(Vals) To UBound(Vals)

        Dim actualFieldName As String
        If index = LBound(Vals) Then
                actualFieldName = "Baseline " & fieldName
        Else
                actualFieldName = "Baseline" & (index + offset - 1) & " " & fieldName
        End If

        Dim taskName As String
        taskName = actualFieldName & " - " & "Task"

        Dim task As task
        Set task = ActiveProject.Tasks.Add(taskName)


        SetTaskField Field:=actualFieldName, value:=Vals(index), TaskID:=task.ID
    Next

End Sub

Sub AddTaskColumn(fieldName As String)
    Dim tableName As String
    tableName = ActiveProject.TaskTableList.Item(1)
    SelectTaskColumn Column:="Resource Names"
    TableEdit name:=tableName, TaskTable:=True, NewFieldName:=fieldName
    TableApply name:=tableName
End Sub

Sub AddResourceColumn(fieldName As String)
    Dim tableName As String
    tableName = ActiveProject.ResourceTableList.Item(1)
    SelectResourceColumn Column:="Code"
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


Sub GenerateTaskTextValues()

    FileNew SummaryInfo:=False
    ActiveProject.BuiltinDocumentProperties("Author").value = "Project User"

    Dim task As task

    Set task = ActiveProject.Tasks.Add("Start is text")
    SetTaskField Field:="Task Mode", value:="Yes", TaskID:=task.ID
    SetTaskField Field:="Start", value:="AAA", TaskID:=task.ID

    Set task = ActiveProject.Tasks.Add("Finish is text")
    SetTaskField Field:="Task Mode", value:="Yes", TaskID:=task.ID
    SetTaskField Field:="Finish", value:="BBB", TaskID:=task.ID

    Set task = ActiveProject.Tasks.Add("Duration is text")
    SetTaskField Field:="Task Mode", value:="Yes", TaskID:=task.ID
    SetTaskField Field:="Duration", value:="CCC", TaskID:=task.ID

    SaveFiles "task-textvalues"

    FileClose pjDoNotSave

End Sub

Sub GenerateProjectProperties()

    FileNew SummaryInfo:=False

    '
    ' Populate the built in document properties
    '
    ActiveProject.BuiltinDocumentProperties("Title").value = "Title"
    ActiveProject.BuiltinDocumentProperties("Subject").value = "Subject"
    ActiveProject.BuiltinDocumentProperties("Author").value = "Author"
    ActiveProject.BuiltinDocumentProperties("Keywords").value = "Keywords"
    ActiveProject.BuiltinDocumentProperties("Comments").value = "Comments"
    ActiveProject.BuiltinDocumentProperties("Template").value = "Template"
    ActiveProject.BuiltinDocumentProperties("Category").value = "Category"
    ActiveProject.BuiltinDocumentProperties("Format").value = "Format"
    ActiveProject.BuiltinDocumentProperties("Manager").value = "Manager"
    ActiveProject.BuiltinDocumentProperties("Company").value = "Company"

    If (Application.Version > 11) Then
        ActiveProject.BuiltinDocumentProperties("Content type").value = "Content type"
        ActiveProject.BuiltinDocumentProperties("Content status").value = "Content status"
        ActiveProject.BuiltinDocumentProperties("Language").value = "Language"
        ActiveProject.BuiltinDocumentProperties("Document version").value = "Document version"
    End If

    '
    ' Populate custom document properties
    '
    ActiveProject.CustomDocumentProperties.Add name:="CustomNumber", _
            LinkToContent:=False, _
            Type:=msoPropertyTypeNumber, _
            value:=1000
    ActiveProject.CustomDocumentProperties.Add name:="CustomFloat", _
            LinkToContent:=False, _
            Type:=msoPropertyTypeFloat, _
            value:=1.5
    ActiveProject.CustomDocumentProperties.Add name:="CustomString", _
            LinkToContent:=False, _
            Type:=msoPropertyTypeString, _
            value:="This is a custom property."
    ActiveProject.CustomDocumentProperties.Add name:="CustomDate", _
            LinkToContent:=False, _
            Type:=msoPropertyTypeDate, _
            value:="01/01/2014"

    SaveFiles "project-properties"

    FileClose pjDoNotSave


End Sub


Sub GenerateBaselines()

    FileNew SummaryInfo:=False

    Dim Vals As Variant

    If CDbl(Application.Version) < 10# Then
        Vals = Array("1")
        AddTasksWithBaselineFieldValues "Cost", Vals

        Vals = Array("11d")
        AddTasksWithBaselineFieldValues "Duration", Vals

        Vals = Array("01/03/2014 09:00")
        AddTasksWithBaselineFieldValues "Finish", Vals

        Vals = Array("01/04/2014 09:00")
        AddTasksWithBaselineFieldValues "Start", Vals

        Vals = Array("51h")
        AddTasksWithBaselineFieldValues "Work", Vals
    Else
        Vals = Array("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11")
        AddTasksWithBaselineFieldValues "Cost", Vals

        Vals = Array("11d", "12d", "13d", "14d", "15d", "16d", "17d", "18d", "19d", "20d", "21d")
        AddTasksWithBaselineFieldValues "Duration", Vals

        Vals = Array("01/03/2014 09:00", "02/03/2014 10:00", "03/03/2014 11:00", "04/03/2014 12:00", "05/03/2014 13:00", "06/03/2014 14:00", "07/03/2014 15:00", "08/03/2014 16:00", "09/03/2014 17:00", "10/03/2014 18:00", "10/03/2014 19:00")
        AddTasksWithBaselineFieldValues "Finish", Vals

        Vals = Array("01/04/2014 09:00", "02/04/2014 10:00", "03/04/2014 11:00", "04/04/2014 12:00", "05/04/2014 13:00", "06/04/2014 14:00", "07/04/2014 15:00", "08/04/2014 16:00", "09/04/2014 17:00", "10/04/2014 18:00", "10/04/2014 19:00")
        AddTasksWithBaselineFieldValues "Start", Vals

        Vals = Array("51h", "52h", "53h", "54h", "55h", "56h", "57h", "58h", "59h", "60h", "61h")
        AddTasksWithBaselineFieldValues "Work", Vals
    End If

    If CDbl(Application.Version) >= 14# Then
        Vals = Array("31d", "32d", "33d", "34d", "35d", "36d", "37d", "38d", "39d", "40d", "41d")
        AddTasksWithBaselineFieldValues "Estimated Duration", Vals

        Vals = Array("01/01/2014 09:00", "02/01/2014 10:00", "03/01/2014 11:00", "04/01/2014 12:00", "05/01/2014 13:00", "06/01/2014 14:00", "07/01/2014 15:00", "08/01/2014 16:00", "09/01/2014 17:00", "10/01/2014 18:00", "10/01/2014 19:00")
        AddTasksWithBaselineFieldValues "Estimated Finish", Vals

        Vals = Array("01/02/2014 09:00", "02/02/2014 10:00", "03/02/2014 11:00", "04/02/2014 12:00", "05/02/2014 13:00", "06/02/2014 14:00", "07/02/2014 15:00", "08/02/2014 16:00", "09/02/2014 17:00", "10/02/2014 18:00", "10/02/2014 19:00")
        AddTasksWithBaselineFieldValues "Estimated Start", Vals

        Vals = Array("11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21")
        AddTasksWithBaselineFieldValues "Fixed Cost", Vals

        Vals = Array("Start", "Prorated", "End", "Start", "Prorated", "End", "Start", "Prorated", "End", "Start", "Prorated")
        AddTasksWithBaselineFieldValues "Fixed Cost Accrual", Vals
    End If

    SaveFiles "task-baselines"

    FileClose pjDoNotSave

End Sub

Sub GenerateProjectValueLists()

    FileNew SummaryInfo:=False

    ' Cost
    CustomFieldProperties FieldID:=pjCustomTaskCost1, Attribute:=pjFieldAttributeValueList
    CustomFieldValueList FieldID:=pjCustomTaskCost1, RestrictToList:=True
    CustomFieldValueListAdd pjCustomTaskCost1, value:="1", Description:="Description 1"
    CustomFieldValueListAdd pjCustomTaskCost1, value:="2", Description:="Description 2"
    CustomFieldValueListAdd pjCustomTaskCost1, value:="3", Description:="Description 3"

    ' Date
    CustomFieldProperties FieldID:=pjCustomTaskDate1, Attribute:=pjFieldAttributeValueList
    CustomFieldValueList FieldID:=pjCustomTaskDate1, RestrictToList:=True
    CustomFieldValueListAdd pjCustomTaskDate1, value:="01/01/2015 08:00", Description:="Description 1"
    CustomFieldValueListAdd pjCustomTaskDate1, value:="02/01/2015 08:00", Description:="Description 2"
    CustomFieldValueListAdd pjCustomTaskDate1, value:="03/01/2015 08:00", Description:="Description 3"

    ' Duration
    CustomFieldProperties FieldID:=pjCustomTaskDuration1, Attribute:=pjFieldAttributeValueList
    CustomFieldValueList FieldID:=pjCustomTaskDuration1, RestrictToList:=True
    CustomFieldValueListAdd pjCustomTaskDuration1, value:="1d", Description:="Description 1"
    CustomFieldValueListAdd pjCustomTaskDuration1, value:="2d", Description:="Description 2"
    CustomFieldValueListAdd pjCustomTaskDuration1, value:="3d", Description:="Description 3"

    ' Number
    CustomFieldProperties FieldID:=pjCustomTaskNumber1, Attribute:=pjFieldAttributeValueList
    CustomFieldValueList FieldID:=pjCustomTaskNumber1, RestrictToList:=True
    CustomFieldValueListAdd pjCustomTaskNumber1, value:="1", Description:="Description 1"
    CustomFieldValueListAdd pjCustomTaskNumber1, value:="2", Description:="Description 2"
    CustomFieldValueListAdd pjCustomTaskNumber1, value:="3", Description:="Description 3"

    ' Text
    CustomFieldProperties FieldID:=pjCustomTaskText1, Attribute:=pjFieldAttributeValueList
    CustomFieldValueList FieldID:=pjCustomTaskText1, RestrictToList:=True
    CustomFieldValueListAdd pjCustomTaskText1, value:="Value 1", Description:="Description 1"
    CustomFieldValueListAdd pjCustomTaskText1, value:="Value 2", Description:="Description 2"
    CustomFieldValueListAdd pjCustomTaskText1, value:="Value 3", Description:="Description 3"

    SaveFiles "project-valuelists"

    FileClose pjDoNotSave
End Sub

Sub GenerateCalendars()
    FileNew SummaryInfo:=False

    ' Add project calendars
    BaseCalendarCreate name:="Calendar1"
    BaseCalendarCreate name:="Calendar2"

    ' Add resource calendars
    ActiveProject.Resources.Add ("Resource One")
    ActiveProject.Resources.Add ("Resource Two")

    SaveFiles "calendar-calendars"

    FileClose pjDoNotSave
End Sub

Sub GenerateResourceAssignments()
    FileNew SummaryInfo:=False

    Dim task1 As task
    Dim task2 As task
    Dim task3 As task

    Set task1 = ActiveProject.Tasks.Add("Task 1")
    Set task2 = ActiveProject.Tasks.Add("Task 2")
    Set task3 = ActiveProject.Tasks.Add("Task 3")

    Dim resource1 As resource
    Dim resource2 As resource
    Dim resource3 As resource

    Set resource1 = ActiveProject.Resources.Add("Resource 1")
    Set resource2 = ActiveProject.Resources.Add("Resource 2")
    Set resource3 = ActiveProject.Resources.Add("Resource 3")

    task1.Start = "04/01/2016 08:00"
    task2.Start = "04/01/2016 08:00"
    task3.Start = "04/01/2016 08:00"

    task1.Duration = "10d"
    task2.Duration = "10d"
    task3.Duration = "10d"

    task1.Assignments.Add ResourceID:=resource1.ID
    task2.Assignments.Add ResourceID:=resource2.ID
    task3.Assignments.Add ResourceID:=resource3.ID

    task2.PercentComplete = 25
    task3.PercentComplete = 50

    SaveFiles "assignment-assignments"

    FileClose pjDoNotSave
End Sub

Sub GenerateResourceAssignmentFlags()
    FileNew SummaryInfo:=False

    ViewApply name:="Resource &Usage"

    Dim taskName As String
    Dim resourceName As String
    Dim flagName As String

    Dim task As task
    Dim resource As resource
    Dim assignment As assignment

    For index = 1 To 20
        taskName = "Task " & index
        Set task = ActiveProject.Tasks.Add(taskName)

        resourceName = "Resource " & index
        Set resource = ActiveProject.Resources.Add(resourceName)

        task.Start = "04/01/2016 08:00"
        task.Duration = "10d"
        task.Assignments.Add ResourceID:=resource.ID

        flagName = "Flag" & index
        TableEdit name:="&Usage", TaskTable:=False, NewFieldName:=flagName
        TableApply name:="&Usage"
    Next

    SelectResourceField Row:=1, Column:="Flag1"
    SetTaskField Field:="Flag1", value:="Yes"

    For index = 2 To 20
        flagName = "Flag" & index
        SelectResourceField Row:=2, Column:=flagName
        SetTaskField Field:=flagName, value:="Yes"
    Next

    SaveFiles "assignment-flags"

    FileClose pjDoNotSave
End Sub

Sub GenerateResourceAssignmentText()
    FileNew SummaryInfo:=False

    ViewApply name:="Resource &Usage"

    Dim taskName As String
    Dim resourceName As String
    Dim fieldName As String

    Dim task As task
    Dim resource As resource
    Dim assignment As assignment

    For index = 1 To 30
        taskName = "Task " & index
        Set task = ActiveProject.Tasks.Add(taskName)

        resourceName = "Resource " & index
        Set resource = ActiveProject.Resources.Add(resourceName)

        task.Start = "04/01/2016 08:00"
        task.Duration = "10d"
        task.Assignments.Add ResourceID:=resource.ID

        fieldName = "Text" & index
        TableEdit name:="&Usage", TaskTable:=False, NewFieldName:=fieldName
        TableApply name:="&Usage"
    Next

    SelectResourceField Row:=1, Column:="Text1"
    SetTaskField Field:="Text1", value:="Text1"

    For index = 2 To 30
        fieldName = "Text" & index
        SelectResourceField Row:=2, Column:=fieldName
        SetTaskField Field:=fieldName, value:=fieldName
    Next

    SaveFiles "assignment-text"

    FileClose pjDoNotSave
End Sub

Sub GenerateResources()
    FileNew SummaryInfo:=False

    Dim resource1 As resource
    Dim resource2 As resource

    Set resource1 = ActiveProject.Resources.Add("Resource 1")
    Set resource2 = ActiveProject.Resources.Add("Resource 2")

    resource1.Code = "Code1"
    resource2.Code = "Code2"

    resource1.CostPerUse = 1.23
    resource2.CostPerUse = 4.56

    resource1.EMailAddress = "resource1@example.com"
    resource2.EMailAddress = "resource2@example.com"

    resource1.Group = "Group1"
    resource2.Group = "Group2"

    resource1.Initials = "R1"
    resource2.Initials = "R2"

    resource1.Notes = "Notes1"
    resource2.Notes = "Notes2"
        
    SaveFiles "resource-misc"

    FileClose pjDoNotSave
End Sub

Sub GenerateResourceTypes()
    FileNew SummaryInfo:=False

	Dim resourceName as string
	Dim resource as resource
	
    For index = 1 To 5
        resourceName = "Cost Resource " & index
        Set resource = ActiveProject.Resources.Add(resourceName)
        If CDbl(Application.Version) > 11 Then
        	resource.Type = pjResourceTypeCost
        Else
        	resource.Type = pjResourceTypeMaterial
        End If
    Next

    For index = 1 To 5
        resourceName = "Material Resource " & index
        Set resource = ActiveProject.Resources.Add(resourceName)
        resource.Type = pjResourceTypeMaterial
    Next

    For index = 1 To 5
        resourceName = "Work Resource " & index
        Set resource = ActiveProject.Resources.Add(resourceName)
        resource.Type = pjResourceTypeWork
    Next

    SaveFiles "resource-type"

    FileClose pjDoNotSave
End Sub

Sub GenerateResourceCustomFlags()

    Dim Vals As Variant

    Vals = Array("Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes")

    FileNew SummaryInfo:=False

    AddResourcesWithCustomFieldValues "Flag", Vals

    SaveFiles "resource-flags"

    FileClose pjDoNotSave

End Sub

Sub GenerateResourceCustomNumbers()

    Dim Vals As Variant

    Vals = Array("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20")

    FileNew SummaryInfo:=False

    AddResourcesWithCustomFieldValues "Number", Vals

    SaveFiles "resource-numbers"

    FileClose pjDoNotSave

End Sub

Sub GenerateResourceCustomText()

    Dim Vals As Variant

    Vals = Array("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30")

    FileNew SummaryInfo:=False

    AddResourcesWithCustomFieldValues "Text", Vals

    SaveFiles "resource-text"

    FileClose pjDoNotSave

End Sub

Sub GenerateDataLinks
    Set Task1 = ActiveProject.Tasks.Add("Task 1")
    SetTaskField Field:="Task Mode", Value:="No", TaskID:=Task1.ID
    SetTaskField Field:="Start", Value:="01/01/2014 09:00", TaskID:=Task1.ID
    SetTaskField Field:="Duration", Value:="5", TaskID:=Task1.ID
    
    Set Task2 = ActiveProject.Tasks.Add("Task 2")
    SetTaskField Field:="Task Mode", Value:="No", TaskID:=Task2.ID
    SetTaskField Field:="Start", Value:="01/01/2014 09:00", TaskID:=Task2.ID
    SetTaskField Field:="Duration", Value:="5", TaskID:=Task2.ID
    
    Set Task3 = ActiveProject.Tasks.Add("Task 3")
    SetTaskField Field:="Task Mode", Value:="No", TaskID:=Task3.ID
    SetTaskField Field:="Start", Value:="01/01/2014 09:00", TaskID:=Task3.ID
    SetTaskField Field:="Duration", Value:="5", TaskID:=Task3.ID

    SelectTaskField RowRelative:=False, Row:=1, Column:="Finish"
    EditCopy
    SelectTaskField RowRelative:=False, Row:=2, Column:="Start"
    EditPasteSpecial Link:=True, Type:=2, DisplayAsIcon:=False

    SelectTaskField RowRelative:=False, Row:=2, Column:="Finish"
    EditCopy
    SelectTaskField RowRelative:=False, Row:=3, Column:="Start"
    EditPasteSpecial Link:=True, Type:=2, DisplayAsIcon:=False
    
    SaveFiles "data-links"

    FileClose pjDoNotSave
End Sub

' If you have a file which contains manually created test data
' you can still save different versions of it by updating and
' using this sub.
Sub SaveCurrentFile()
    SaveFiles "<your base filename here>"
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
        ' Project 2016
        Case "16.0"
            Dim productName As String
            
            
            productName = "project2016"
            
            ' Doesn't work correctly - need a different way to identify Project 2019
            'If Split(Application.Build, ".")(2) >= 10730 Then
            '    productName = "project2019"
            'Else
            '    productName = "project2016"
            'End If
            
            CalculateAll
            FileSaveAs name:=Filename & "-" & productName & "-mpp14.mpp"

            CalculateAll
            FileSaveAs name:=Filename & "-" & productName & "-mspdi.xml", FormatID:="MSProject.XML"

            CalculateAll
            FileSaveAs name:=Filename & "-" & productName & "-mpp12.mpp", FormatID:="MSProject.MPP.12"

        ' Project 2013
        Case "15.0"
            CalculateAll
            FileSaveAs name:=Filename & "-project2013-mpp14.mpp"

            CalculateAll
            FileSaveAs name:=Filename & "-project2013-mspdi.xml", FormatID:="MSProject.XML"

            CalculateAll
            FileSaveAs name:=Filename & "-project2013-mpp12.mpp", FormatID:="MSProject.MPP.12"

            CalculateAll
            FileSaveAs name:=Filename & "-project2013-mpp9.mpp", FormatID:="MSProject.MPP.9"

        ' Project 2010
        Case "14.0"
            CalculateAll
            FileSaveAs name:=Filename & "-project2010-mpp14.mpp"

            CalculateAll
            FileSaveAs name:=Filename & "-project2010-mspdi.xml", FormatID:="MSProject.XML"

            CalculateAll
            FileSaveAs name:=Filename & "-project2010-mpp12.mpp", FormatID:="MSProject.MPP.12"

            CalculateAll
            FileSaveAs name:=Filename & "-project2010-mpp9.mpp", FormatID:="MSProject.MPP.9"

        ' Project 2007
        Case "12.0"
            CalculateAll
            FileSaveAs name:=Filename & "-project2007-mpp12.mpp"

            CalculateAll
            FileSaveAs name:=Filename & "-project2007-mspdi.xml", FormatID:="MSProject.XML"

            CalculateAll
            FileSaveAs name:=Filename & "-project2007-mpp9.mpp", FormatID:="MSProject.MPP.9"

        ' Project 2003
        Case "11.0"
            CalculateAll
            FileSaveAs name:=Filename & "-project2003-mpp9.mpp"

            CalculateAll
            FileSaveAs name:=Filename & "-project2003-mspdi.xml", FormatID:="MSProject.XML"

            CalculateAll
            FileSaveAs name:="<" & Filename & "-project2003-mpd9.mpd>\" & FilenameBase & "-project2003-mpd9", FormatID:="MSProject.MPD.9"

            'CalculateAll
            'FileSaveAs name:=Filename & "-project2003-mpp8.mpp", FormatID:="MSProject.MPP.8"

        ' Project 2002
        Case "10.0"
            CalculateAll
            FileSaveAs name:=Filename & "-project2002-mpp9.mpp"

            CalculateAll
            FileSaveAs name:=Filename & "-project2002-mspdi.xml", FormatID:="MSProject.XML"

            CalculateAll
            FileSaveAs name:="<" & Filename & "-project2002-mpd9.mpd>\" & FilenameBase & "-project2002-mpd9", FormatID:="MSProject.MPD.9"

            'CalculateAll
            'FileSaveAs name:=Filename & "-project2002-mpp8.mpp", FormatID:="MSProject.MPP.8"

        ' Project 2000
        Case "9.0"
            CalculateAll
            FileSaveAs name:=Filename & "-project2000-mpp9.mpp"

            CalculateAll
            FileSaveAs name:="<" & Filename & "-project2000-mpd9.mpd>\" & FilenameBase & "-project2000-mpd9", FormatID:="MSProject.MPD.9"

            'CalculateAll
            'FileSaveAs name:=Filename & "-project2000-mpp8.mpp", FormatID:="MSProject.MPP.8"

        ' Project 98
        Case "8.0"
            'CalculateAll
            'FileSaveAs name:=Filename & "-project98-mpp8.mpp"

            'CalculateAll
            'FileSaveAs name:="<" & Filename & "-project98-mpd8.mpd>\" & FilenameBase & "-project98-mpd8", FormatID:="MSProject.MPD.8"

            CalculateAll
            FileSaveAs name:=Filename & "-project98.mpx", FormatID:="MSProject.MPX.8"

        Case Else
            Debug.Print "Unknown Version"

    End Select
End Sub




