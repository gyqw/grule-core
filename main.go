package main

import (
	"fmt"
	"log"

	"github.com/antlr/antlr4/runtime/Go/antlr"
)

func main() {
	fs, err := antlr.NewFileStream("input_file")
	if err != nil {
		log.Fatal(err)
	}
	fmt.Println(fs)
}
