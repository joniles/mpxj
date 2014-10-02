Attribute VB_Name = "GenerateTestData"

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
        
        Dim fieldConstant As Long
        fieldConstant = FieldNameToFieldConstant(fieldName, pjTask)
        task.SetField fieldConstant, Vals(index)
        
        SelectTaskColumn Column:="Add New Column"
        TableEditEx name:="&Entry", TaskTable:=True, NewFieldName:=fieldName
        TableApply name:="&Entry"
    Next

End Sub
    
    
Sub GenerateTaskFlags()

    Dim Vals As Variant
    
    Vals = Array("Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes", "Yes")
    
    FileNew Template:=""
    
    PopulateTaskFields "Flag", Vals
    
    SaveFiles "task-flags", "2010"
            
End Sub

Sub SaveFiles(Filename As String, Version As String)
    CalculateAll
    FileSaveAs name:=Filename & "-project" & Version & "-mpp14.mpp", FormatID:="MSProject.MPP"
    
    CalculateAll
    FileSaveAs name:=Filename & "-project" & Version & "-mpp12.mpp", FormatID:="MSProject.MPP.12"
    
    CalculateAll
    FileSaveAs name:=Filename & "-project" & Version & "-mpp9.mpp", FormatID:="MSProject.MPP.9"
End Sub

Sub SaveFilesFromProject2007()
    Dim Filename As String
    Filename = "task-flags"
    
    Dim Version As String
    Version = "2007"
    
    CalculateAll
    FileSaveAs name:=Filename & "-project" & Version & "-mpp12.mpp", FormatID:="MSProject.MPP.12"
    
    CalculateAll
    FileSaveAs name:=Filename & "-project" & Version & "-mpp9.mpp", FormatID:="MSProject.MPP.9"
End Sub

Sub SaveFilesFromProject2003()
    Dim Filename As String
    Filename = "task-flags"
    
    Dim Version As String
    Version = "2003"
    
    CalculateAll
    FileSaveAs name:=Filename & "-project" & Version & "-mpp9.mpp", FormatID:="MSProject.MPP.9"
    
    CalculateAll
    FileSaveAs name:=Filename & "-project" & Version & "-mpp8.mpp", FormatID:="MSProject.MPP.8"
End Sub